package com.archer.matching_card_game.three_cards;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] Cards;
    private String[][] Position;
    private int lengthOfArray;


    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return lengthOfArray;
    }

    public Object getItem(int position) {
        if(Position[position][1] == null)
            return Position[position][0];

        return Position[position][((int)(Math.random()*100))%2];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int size = HelperClass.ConvertToPx(mContext,90);
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(Cards[position]);
        return imageView;
    }


    public void createCardsSet(ImageView[][] allCards, int[][] imgResId, int rows, int cols) {
        SparseArray<Integer> image_count = new SparseArray<>();
        int length, index = 0;

        length = getLengthOfArray(allCards,imgResId,rows,cols);
        Cards = new Integer[length];
        Position = new String[length][2];
        for(int r=0;r<rows;r++) {
            for (int c = 0; c < cols; c++) {
                if (allCards[r][c] != null) {
                    if (image_count.indexOfKey(imgResId[r][c])<0) {
                        image_count.put(imgResId[r][c], index);
                        Position[index][0] = r + HelperClass.DELIMITER + c;
                        Cards[index++] = imgResId[r][c];
                    } else {
                        int existing_index_card = image_count.get(imgResId[r][c]);
                        Position[existing_index_card][1] = r + HelperClass.DELIMITER + c;
                    }
                }
            }
        }
    }

    public int getLengthOfArray(ImageView[][] allCards, int[][] imgResId, int rows, int cols) {
        SparseArray<Integer> image_count = new SparseArray<>();
        for(int r=0;r<rows;r++)
        {
            for (int c=0;c<cols;c++)
            {
                if(allCards[r][c]!= null)
                {
                    if(image_count.indexOfKey(imgResId[r][c])<0)
                    {
                        image_count.put(imgResId[r][c],1);
                        lengthOfArray++;
                    }
                    else
                    {
                        image_count.put(imgResId[r][c],2);
                    }
                }
            }
        }
        return lengthOfArray;
    }


}