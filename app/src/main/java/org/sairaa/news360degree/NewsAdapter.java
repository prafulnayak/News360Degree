package org.sairaa.news360degree;

import android.arch.paging.PagedListAdapter;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.sairaa.news360degree.db.News;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NewsAdapter extends PagedListAdapter<News,NewsAdapter.NewsViewHolder> {
    private Context mCtx;

    public NewsAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
    }
    private static DiffUtil.ItemCallback<News> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<News>() {
                @Override
                public boolean areItemsTheSame(@NonNull News oldItem, @NonNull News newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull News oldItem, @NonNull News newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_users, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        final News news = getItem(position);
        if(news != null){
            if(!news.getUrlToImage().isEmpty()){
//                Glide.with(mCtx)
//                        .load(news.getUrlToImage())
//                        .into(holder.imageView);

                holder.imageView.setImageBitmap(downloadImageFromInternal(news.getUrlToImage()));
//                Toast.makeText(mCtx,"image"+news.getUrlToImage(),Toast.LENGTH_SHORT).show();

            }else {
                holder.imageView.setImageBitmap(null);
//                Toast.makeText(mCtx,"no image"+news.getUrlToImage(),Toast.LENGTH_SHORT).show();
            }
            holder.heading.setText(news.getTitle());
            holder.description.setText(news.getDescription());
            holder.author.setText(news.getAuthor());
            holder.publishDate.setText(news.getPublishedAt());
            holder.shareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Uri imageUri = Uri.parse(news.getUrlToImage());
                    if(!news.getTitle().isEmpty()){
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        //Target whatsapp:
                        shareIntent.setPackage("com.whatsapp");
                        //Add text and then Image URI
                        shareIntent.putExtra(Intent.EXTRA_TEXT, news.getTitle());
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        shareIntent.setType("text/plain");
//                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            mCtx.startActivity(shareIntent);
                        } catch (android.content.ActivityNotFoundException ex) {

                            Toast.makeText(mCtx,"Whatsapp have not been installed.",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            Log.e("data1",news.getTitle());
        }
//        news.setUrlToImage("");
    }

    private Bitmap downloadImageFromInternal(String imageUriPath) {
        Bitmap b = null;
        try {
            File f=new File(imageUriPath);
//            Log.i(LOG_TAGADAPTER,"Test "+String.valueOf(f));
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //iImageView.setImageBitmap(b);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView heading, description, publishDate, author;
        ImageView imageView, shareImage, openWindow;
        public NewsViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            heading = itemView.findViewById(R.id.heading);
            description = itemView.findViewById(R.id.description);
            publishDate = itemView.findViewById(R.id.publish_date);
            author = itemView.findViewById(R.id.author_name);
            shareImage = itemView.findViewById(R.id.share);
            openWindow = itemView.findViewById(R.id.open_window);

        }
    }
}
