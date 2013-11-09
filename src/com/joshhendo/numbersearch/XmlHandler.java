package com.joshhendo.numbersearch;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * com.joshhendo.numbersearch Copyright 2011
 * User: Josh
 * Date: 12/08/11
 * Time: 9:54 PM
 */
public class XmlHandler extends DefaultHandler
{

     @Override
     public void startElement(String uri, String localName, String qName,
     Attributes attributes) throws SAXException
     {
     // handle elements open
     }

     @Override
     public void endElement(String uri, String localName, String qName)
     throws SAXException {
     // handle element close
     }

     @Override
     public void characters(char[] ch, int start, int length)
     throws SAXException {
     // handle tag characters <blah>stuff</blah>
     }

 }
