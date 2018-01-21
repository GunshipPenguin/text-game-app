package com.gunshippenguin.textgame;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gunshippenguin.textgame.events.DisplayableInterface;

import java.util.ArrayList;
import java.util.Random;

public class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.ViewHolder> {
    private ArrayList<DisplayableInterface> mDataset;
    private Context ctx;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public static LinearLayout mWrapper;
        public static CardView cardView;
        public static TextView content;
        public static TextView smallDetail;
        public static ImageView image;
        public static FrameLayout marker;

        public ViewHolder(LinearLayout v) {
            super(v);
            mWrapper = v;
            cardView = (CardView) v.findViewById(R.id.cv);
            cardView.setCardElevation(8);
            content = (TextView)cardView.findViewById(R.id.content);
            smallDetail = (TextView)cardView.findViewById(R.id.small_detail);
            image = (ImageView)cardView.findViewById(R.id.image);
            marker = (FrameLayout)cardView.findViewById(R.id.marker);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StreamAdapter(Context context, ArrayList<DisplayableInterface> myDataset) {
        this.ctx = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StreamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DisplayableInterface data = mDataset.get(position);
        if (data.isChatMessage()){
            // show profile box and name section
            holder.smallDetail.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);

            String number = data.getSenderNumber();
            byte[] byteArray = getImageByNumber(number);
            if (getContactNameByNumber(number).equals("?")){
                holder.smallDetail.setText("Me"); // name
            } else {
                holder.smallDetail.setText(getContactNameByNumber(number)); // name
            }

            if (byteArray != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                holder.image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 150, 150, false));
            }
        }
        holder.content.setText(data.getMessage());
        // ctx.getResources().getColor(R.color.lightGrey)
        holder.marker.setBackgroundColor(data.getColor(ctx));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getContactNameByNumber(String number) {
        String name = "?";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    public byte[] getImageByNumber(String contactNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber));

        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor contactLookupCursor = contentResolver.query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber)), new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return openPhoto(phoneContactID);
    }



    public byte[] openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = ctx.getContentResolver().query(photoUri,
                new String[] {Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return data; // byte[]
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}
