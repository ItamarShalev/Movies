package com.movies.adapter;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.movies.R;
import com.movies.activities.moviedetails.MovieDetailsActivity;
import com.movies.data.MovieData;
import com.movies.utils.ImageHandler;

import java.util.List;

public class AdapterMovieData extends RecyclerView.Adapter<AdapterMovieData.Holder> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<MovieData> movieDataList;
    private ImageHandler imageHandler;
    private int sizeItemHeight;
    private int sizeScreenWidth;
    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Holder holder = (Holder) v.getTag();
            MovieData movieData = movieDataList.get(holder.getAdapterPosition());
            Intent intent = new Intent(activity, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.TAG_MOVIE_DATA, movieData);

            Pair[] pairs = new Pair[3];
            pairs[0] = new Pair<View, String>(holder.mainParentCardView, activity.getString(R.string.image_movie_transition));
            pairs[1] = new Pair<View, String>(holder.nameMovieTextView, activity.getString(R.string.name_movie_transition));
            pairs[2] = new Pair<View, String>(holder.releaseYearTextView, activity.getString(R.string.release_year_movie_transition));

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, pairs);
            activity.startActivity(intent, activityOptions.toBundle());
        }
    };


    public AdapterMovieData(Activity activity, List<MovieData> movieDataList, int sizeItemHeight, int sizeScreenWidth) {
        this.activity = activity;
        this.movieDataList = movieDataList;
        inflater = LayoutInflater.from(activity);
        imageHandler = new ImageHandler(activity.getFilesDir().getPath());
        this.sizeItemHeight = sizeItemHeight;
        this.sizeScreenWidth = sizeScreenWidth;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_movie_data, parent, false);
        setSize(itemView);
        //when clicked move to detailActivity
        itemView.setOnClickListener(onClickItem);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.itemView.setTag(holder);
        MovieData movieData = movieDataList.get(position);
        holder.loadingImageProgressBar.setVisibility(View.VISIBLE);
        holder.imageMovieImageView.setTag(position);
        holder.imageMovieImageView.setImageBitmap(null);
        imageHandler.loadImageAndSave(movieData.getImageUrl(), holder.imageMovieImageView, holder.loadingImageProgressBar);
        holder.nameMovieTextView.setText(movieData.getName());
        holder.releaseYearTextView.setText(("(" + movieData.getReleaseYear() + ")"));
    }


    @Override
    public int getItemCount() {
        return movieDataList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        imageHandler.finish();
    }

    private void setSize(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = sizeScreenWidth;
        layoutParams.height = sizeItemHeight;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemViewType(int position) {
        boolean isLastItem = position == movieDataList.size() - 1;
        return isLastItem ? 0 : 1;
    }

    static class Holder extends RecyclerView.ViewHolder {

        private ImageView imageMovieImageView;
        private TextView nameMovieTextView;
        private TextView releaseYearTextView;
        private ProgressBar loadingImageProgressBar;
        private CardView mainParentCardView;

        Holder(View itemView) {
            super(itemView);
            mainParentCardView = itemView.findViewById(R.id.main_parent_card_view);
            imageMovieImageView = itemView.findViewById(R.id.image_movie_image_view);
            nameMovieTextView = itemView.findViewById(R.id.name_movie_text_view);
            releaseYearTextView = itemView.findViewById(R.id.release_year_text_view);
            loadingImageProgressBar = itemView.findViewById(R.id.loading_image_progress_bar);
        }
    }
}
