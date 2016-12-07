package com.example.prora.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getName();
	boolean success = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		domParser();
		if(success) Log.d(TAG, "onCreate: parse success ");
	}

	public void domParser()
	{
		try {
			//get permission
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
				Intent intent =
						new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
				intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
						getPackageName());
				startActivity(intent);
			}
			boolean duplicate = false;
			Cursor sCursor = getContentResolver().query(Uri.parse("content://sms"),null,null,null,null);
			sCursor.moveToFirst();
			DocumentBuilderFactory fac= DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= fac.newDocumentBuilder();
			FileInputStream fIn=new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SCBS_SMS/1445410687768/SMS_1445410687768.xml");
			Document doc=builder.parse(fIn);
			Element root= doc.getDocumentElement();
			NodeList list= root.getChildNodes();
			String datashow="";
			String SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19 = "content://sms";
			for(int i=0;i<list.getLength();i++)
			{
				Node node=list.item(i);
				if(node instanceof Element)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
						Element item =(Element) node;
						NodeList listChild=item.getElementsByTagName("_id");
						int _id = Integer.parseInt(listChild.item(0).getTextContent()) ;
						listChild=item.getElementsByTagName("thread_id");
						int thread_id = Integer.parseInt(listChild.item(0).getTextContent());
						listChild=item.getElementsByTagName("address");
						String address =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("date");
						String date = listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("date_sent");
						int date_sent =Integer.parseInt(listChild.item(0).getTextContent());
						listChild=item.getElementsByTagName("read");
						int read =Integer.parseInt(listChild.item(0).getTextContent());
						listChild=item.getElementsByTagName("status");
						int status =Integer.parseInt(listChild.item(0).getTextContent());
						listChild=item.getElementsByTagName("type");
						int type =Integer.parseInt(listChild.item(0).getTextContent());
						listChild=item.getElementsByTagName("body");
						String body =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("locked");
						int locked =Integer.parseInt(listChild.item(0).getTextContent());

						datashow+=_id+"-"+thread_id+"-"+address+"-"+date+"-"+date_sent+"_"+read+"-"+status+"-"+type+"-"+body+"-"+locked
								+"\n---------\n";

						ContentValues values = new ContentValues();
						values.put("address",address);
						values.put("body",body);
						values.put("type", type);
						values.put("date", date);
						values.put("read", read);
						for (int j = 0; j < sCursor.getCount(); j++){
							if(address.equals(sCursor.getString(sCursor.getColumnIndex(Telephony.Sms.ADDRESS)))&&date.equals(sCursor.getString(sCursor.getColumnIndex(Telephony.Sms.DATE)))){
								duplicate=true;
								break;
							}
						}
                        /*values.put("address", 1);
                        values.put("_id", _id);
                        values.put("thread_id", thread_id);
                        values.put("date", date);
                        values.put("date_sent", date_sent);
                        values.put("read", read);
                        values.put("status", status);
                        values.put("body", body);
                        values.put("locked", locked);
                        values.put("protocol", 0);
                        values.put("service_center", "");*/
						if(duplicate == false){
							getContentResolver().insert(Uri.parse("content://sms/"), values);
							// getContentResolver().delete(Uri.parse("content://sms/conversations/-1"), null, null);
							values.clear();
						}

					} else{
						Element item =(Element) node;
						NodeList listChild=item.getElementsByTagName("thread_id");
						String thread_id =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("address");
						String address =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("date");
						String date =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("read");
						String read =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("status");
						String status =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("type");
						String type =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("body");
						String body =listChild.item(0).getTextContent();
						listChild=item.getElementsByTagName("locked");
						String locked =listChild.item(0).getTextContent();

						datashow+=thread_id+"-"+address+"-"+date+"_"+read+"-"+status+"-"+type+"-"+body+"-"+locked
								+"\n---------\n";

						ContentValues values = new ContentValues();
						// values.put("thread_id", thread_id);
						values.put("address", address);
						values.put("date", date);
						values.put("read", read);
						// values.put("status", status);
						values.put("type", type);
						values.put("body", body);

						for (int j = 0; j < sCursor.getCount(); j++){
							if(address.equals(sCursor.getString(sCursor.getColumnIndex("address")))&&date.equals(sCursor.getString(sCursor.getColumnIndex("date")))){
								duplicate=true;
								break;
							}
						}
						// values.put("locked", locked);
						if(duplicate == false) {
							getContentResolver().insert(Uri.parse(SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19), values);
							// getContentResolver().delete(Uri.parse("content://sms/conversations/-1"), null, null);
							values.clear();
						}

					}
				}
			}
			Log.d("ParseXML",datashow);
			success = true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
