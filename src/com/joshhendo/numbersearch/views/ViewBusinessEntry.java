package com.joshhendo.numbersearch.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.joshhendo.numbersearch.R;
import com.joshhendo.numbersearch.base.CommonFunctions;
import com.joshhendo.numbersearch.base.EntryScreen;
import com.joshhendo.numbersearch.structures.BusinessSearchEntry;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: josh
 * Date: 18/06/11
 * Time: 12:26 AM
 */
public class ViewBusinessEntry extends EntryScreen
{
    private BusinessSearchEntry businessSearchEntry = null;
    private String businessSearchEntryName = null;
    private Uri selectedContact = null;
    private Integer selectedContactID = null;
    private Context gContext = null;

    private ProgressDialog m_ProgressDialog = null;
    private Runnable getDetails;
    String output = null;

    public enum AddTask { SELECTEDCONTACT, NEWCONTACT, OTHERCONTACT }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessentry);
        
        gContext = this;

        // Get the fetcher data that was passed.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            try
            {
                businessSearchEntry = (BusinessSearchEntry) extras.getSerializable("ENTRY");
            }
            catch (Exception e)
            {
                // whoops
            }
        }

        // Add the name extracted from the previous screen. This will be over written if a
        // business name is extrated off the details page, but this doesn't always happen for some
        // reason, leaving the business name as "View Entry"
        if (businessSearchEntry.getName() != null)
        {
            TextView text = (TextView) findViewById(R.id.entry_text_business_title);
            text.setText(businessSearchEntry.getName());
            setLongClickCopyToClipboard(text, gContext);
        }

        //TextView name = (TextView) findViewById(R.id.text_entry_name);

        //name.setText(businessSearchEntry.getName());

        

        getDetails = new Runnable()
        {
            public void run()
            {
                fetchDetails(businessSearchEntry.getUrl(), businessSearchEntry.getCookieStore());
            }
        };

        final Thread thread = new Thread(null, getDetails, "MangentoBackground");
        thread.start();

        m_ProgressDialog = new ProgressDialog(ViewBusinessEntry.this);
        m_ProgressDialog.setCancelable(true);
        m_ProgressDialog.setTitle(gContext.getString(R.string.please_wait));
        m_ProgressDialog.setMessage(gContext.getString(R.string.retrieving_data));
        m_ProgressDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener()
                {
                    public void onCancel(DialogInterface dialogInterface)
                    {
                        //thread.stop();
                        ((Activity) gContext).finish();
                    }
                }
        );
        m_ProgressDialog.show();
    }

    private void fetchDetails(String URL, CookieStore cookieStore)
    {
        URL = URL.replaceAll(";jsessionid=.*?\\.wpmserver2-1", "");
        URL = URL.replaceAll("&amp;", "&");
        URL = "http://mobile.whitepages.com.au" + URL;

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        HttpClient client = new DefaultHttpClient();
        HttpProtocolParams.setUserAgent(client.getParams(), "Mozilla/5.0 (Linux; U; Android 2.3; en-us) AppleWebKit/999+ (KHTML, like Gecko) Safari/999.9");

        HttpPost post = new HttpPost(URL);
        post.addHeader("Referer", "http://mobile.whitepages.com.au/");

        output = "";
        try
        {
            HttpResponse response = client.execute(post, localContext);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                output += line;
            }
        }
        catch (IOException e)
        {
            return ;
        }

        runOnUiThread(returnRes);
    }

    private Runnable returnRes = new Runnable()
    {
        public void run()
        {
            // Determine if this is the final destination.
            String regex = "wpm.ds.notFinalDestination = false;\\s*window.location=\"(.*?)\";";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(output);

            if (matcher.find())
            {
                // Gah, now we've got to close this activity, and start a new one with the new URL.
                String newURL = matcher.group(1);

                businessSearchEntry.setUrl(newURL);

                m_ProgressDialog.dismiss();
                finish();

                // Redirect to the new page.
                Intent intent = new Intent(gContext, ViewBusinessEntry.class);
                intent.putExtra("ENTRY", businessSearchEntry);
                gContext.startActivity(intent);
            }
            else
            {
                HtmlCleaner cleaner = new HtmlCleaner();
                TagNode node = cleaner.clean(output);
                recursively_process(node.getElementsByName("div", true));

                m_ProgressDialog.dismiss();
            }
        }
    };

    private String extractContent(List<HtmlNode> htmlNodes)
    {
        String returnValue = "";

        for (HtmlNode htmlNode : htmlNodes)
        {
            ContentNode contentNode = null;
            try
            {
                contentNode = (ContentNode) htmlNode;
            }
            catch (Exception e)
            {
                continue;
            }

            returnValue += contentNode.getContent().toString();
        }

        returnValue = CommonFunctions.fixText(returnValue);

        return returnValue;
    }

    private void recursively_process(TagNode[] nodes)
    {
        TextView currentAddressView = null;
        ImageButton currentAddressMapButton = null;
        ImageButton currentAddressNavButton = null;
        String currentAddressText = null;

        for (int i = 0; i < nodes.length; i++)
        {
            TagNode node = nodes[i];

            try
            {
                node.getAttributeByName("class").length();
            }
            catch (NullPointerException e)
            {
                continue;
            }



            // It's the title of the whole entry!
            if (node.getAttributeByName("class").matches("detailTitle (bold)* ") && node.getAttributeByName("id").matches("listingName"))
            {
                List<HtmlNode> htmlNodes = node.getChildren();

                TextView text = (TextView) findViewById(R.id.entry_text_business_title);
                text.setText(extractContent(htmlNodes));
                setLongClickCopyToClipboard(text, gContext);

                continue;
            }

            // It's a title!
            if (node.getAttributeByName("class").matches("detailInfo textLines (bold )*"))
            {
                List<HtmlNode> htmlNodes = node.getChildren();
                //System.out.println("Title: " + extractContent(htmlNodes));

                ViewGroup parent = (ViewGroup) findViewById(R.id.vertical_container);
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.businessviewtitle, null);
                TextView text = (TextView) view.findViewById(R.id.text_title);
                text.setText(extractContent(htmlNodes));
                parent.addView(view);
                setLongClickCopyToClipboard(text, gContext);

                continue;
            }

            // It's a call link!
            if (node.getAttributeByName("class").matches("callLink") || node.getAttributeByName("class").matches("phoneNumber"))
            {
                String phoneNumber = "";


                if (node.getAttributeByName("class").matches("callLink"))
                {
                    final List<HtmlNode> htmlNodes = ((TagNode) node.getChildren().get(0)).getChildren();
                    phoneNumber = extractContent(htmlNodes);
                }
                else if (node.getAttributeByName("class").matches("phoneNumber"))
                {
                    final List<HtmlNode> htmlNodes = node.getChildren();
                    if (htmlNodes.size() >= 3 && htmlNodes.get(1).toString().equals("a"))
                    {
                        phoneNumber = ((TagNode) htmlNodes.get(1)).getChildren().get(0).toString();
                    }
                }

                ViewGroup parent = (ViewGroup) findViewById(R.id.vertical_container);
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.businessviewphone, null);
                TextView text = (TextView) view.findViewById(R.id.text_entry_phone);
                text.setText(phoneNumber);
                setLongClickCopyToClipboard(text, gContext);

                // Configure call button
                ImageButton button = (ImageButton) view.findViewById(R.id.button_call);
                button.setClickable(true);
                button.setFocusable(true);
                button.setBackgroundResource(android.R.drawable.menuitem_background);

                final String finalPhoneNumber = phoneNumber;
                button.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + CommonFunctions.getOnlyNumerics(finalPhoneNumber)));
                        gContext.startActivity(intent);
                    }
                });

                parent.addView(view);

                continue;
            }

            // Fax number or other information
            if (node.getAttributeByName("class").matches("detailInfo"))
            {
                final List<HtmlNode> htmlNodes = node.getChildren();

                if ( htmlNodes.size() >= 2 && htmlNodes.get(1).toString().equals("a"))
                {
                    // it's a link
                    final String link = CommonFunctions.checkURL(((TagNode) ((TagNode) htmlNodes.get(1)).getChildren().get(0)).getChildren().get(0).toString().trim());


                    ViewGroup parent = (ViewGroup) findViewById(R.id.vertical_container);
                    View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.businessviewlink, null);
                    TextView text = (TextView) view.findViewById(R.id.text_entry_link);
                    text.setText(link);
                    setLongClickCopyToClipboard(text, gContext);

                    // Configure link button
                    ImageButton button = (ImageButton) view.findViewById(R.id.button_goto);
                    button.setClickable(true);
                    button.setFocusable(true);
                    button.setBackgroundResource(android.R.drawable.menuitem_background);

                    button.setOnClickListener(new View.OnClickListener()
                    {
                        public void onClick(View view)
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(link));
                            gContext.startActivity(intent);
                        }
                    });

                    parent.addView(view);
                }
                else
                {
                    // it's something else
                    ViewGroup parent = (ViewGroup) findViewById(R.id.vertical_container);
                    View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.businessviewtitle, null);
                    TextView text = (TextView) view.findViewById(R.id.text_title);
                    text.setText(extractContent(htmlNodes));
                    parent.addView(view);
                    setLongClickCopyToClipboard(text, gContext);
                }

                continue;
            }

            // Address line 1
            if (node.getAttributeByName("class").matches("detailInfo addressLine1"))
            {
                List<HtmlNode> htmlNodes = node.getChildren();
                currentAddressText = extractContent(htmlNodes);
                ViewGroup parent = (ViewGroup) findViewById(R.id.vertical_container);
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.businessviewaddress, null);
                currentAddressView = (TextView) view.findViewById(R.id.text_entry_address);
                currentAddressView.setText(currentAddressText);
                parent.addView(view);
                setLongClickCopyToClipboard(currentAddressView, gContext);

                // Configure map button
                currentAddressMapButton = (ImageButton) view.findViewById(R.id.button_map);

                // Determine if intent is available
                if (!isIntentAvailable(this, Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=sydney,nsw,australia")))
                    currentAddressMapButton.setEnabled(false);

                currentAddressMapButton.setClickable(true);
                currentAddressMapButton.setFocusable(true);
                currentAddressMapButton.setBackgroundResource(android.R.drawable.menuitem_background);

                final String finalCurrentAddressText = currentAddressText;
                currentAddressMapButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("geo:0,0?q=" + CommonFunctions.removeNewLines(finalCurrentAddressText)));
                        gContext.startActivity(intent);
                    }
                });

                // Configure NAVIGATE button
                currentAddressNavButton = (ImageButton) view.findViewById(R.id.button_navigate);

                // Determine if intent is available
                if (!isIntentAvailable(this, Intent.ACTION_VIEW, Uri.parse("google.navigation:q=sydney,nsw,australia")))
                    currentAddressNavButton.setEnabled(false);

                currentAddressNavButton.setClickable(true);
                currentAddressNavButton.setFocusable(true);
                currentAddressNavButton.setBackgroundResource(android.R.drawable.menuitem_background);

                currentAddressNavButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("google.navigation:q=" + CommonFunctions.removeNewLines(finalCurrentAddressText)));
                        gContext.startActivity(intent);
                    }
                });


                continue;
            }

            if (node.getAttributeByName("class").matches("detailInfo addressLine2") || node.getAttributeByName("class").matches("detailInfo addressLine3"))
            {
                if ( currentAddressText == null || currentAddressView == null || currentAddressMapButton == null || currentAddressNavButton == null)
                    continue;

                List<HtmlNode> htmlNodes = node.getChildren();
                currentAddressText += "\n" + extractContent(htmlNodes);
                currentAddressView.setText(currentAddressText);

                // Update MAP button
                final String finalCurrentAddressText = currentAddressText;
                currentAddressMapButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("geo:0,0?q=" + CommonFunctions.removeNewLines(finalCurrentAddressText)));
                        gContext.startActivity(intent);
                    }
                });

                // Update NAV button
                currentAddressNavButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("google.navigation:q=" + CommonFunctions.removeNewLines(finalCurrentAddressText)));
                        gContext.startActivity(intent);
                    }
                });
            }
        }
    }

    public static boolean isIntentAvailable(Context context, String action, Uri uri)
    {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(action);
        if ( uri != null )
            intent.setData(uri);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
