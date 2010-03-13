package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.view.adapter.HelpImageBrowserAdapter;

/**
 * Activity to show image help files with their captions. Clicking an item in
 * the gallery at the top will switch the image (and its caption) in the main
 * panel.
 * 
 * <b>this activity expects that the extras bundle contains 2 ArrayLists of the
 * same size (one containing image urls, the other containing their
 * captions).</b>
 * 
 * TODO: make sure we don't re-download pictures when we don't need to
 * 
 * @author Christopher Fagiani
 * 
 */
public class ImageBrowserActivity extends Activity implements
		OnItemClickListener {

	private Gallery gallery;
	private ImageView mainImageView;
	private TextView captionTextView;
	private HelpImageBrowserAdapter imageAdapter;
	private ArrayList<String> imageUrls;
	private ArrayList<String> captions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagebrowser);
		gallery = (Gallery) findViewById(R.id.imagebrowsergallery);
		captionTextView = (TextView) findViewById(R.id.captiontextview);
		mainImageView = (ImageView) findViewById(R.id.mainimageview);

		Bundle extras = getIntent().getExtras();
		imageUrls = extras.getStringArrayList(ConstantUtil.IMAGE_URL_LIST_KEY);
		captions = extras
				.getStringArrayList(ConstantUtil.IMAGE_CAPTION_LIST_KEY);

		imageAdapter = new HelpImageBrowserAdapter(this, imageUrls);
		if (imageUrls.size() == 1) {
			gallery.setVisibility(View.GONE);
		} else {
			Toast.makeText(this,R.string.imagehelpviewmessage, Toast.LENGTH_LONG).show();
			gallery.setVisibility(View.VISIBLE);
		}

		gallery.setAdapter(imageAdapter);
		gallery.setOnItemClickListener(this);
		mainImageView.setImageBitmap(imageAdapter.getImageBitmap(0));
		captionTextView.setText(captions.get(0));		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		mainImageView.setImageBitmap(imageAdapter.getImageBitmap(position));
		captionTextView.setText(captions.get(position));
	}

}
