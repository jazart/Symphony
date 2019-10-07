package com.jazart.symphony.posts;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.Slide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jazart.symphony.R;
import com.jazart.symphony.di.AppKt;
import com.jazart.symphony.di.SimpleViewModelFactory;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entities.Post;


public class NewPostFragment extends Fragment {
    @BindView(R.id.new_post_body_til)
    TextInputLayout mBodyTil;

    @BindView(R.id.new_post_body)
    TextInputEditText mBody;

    @BindView(R.id.addMediaBtn)
    AppCompatImageView mAddMediaBtn;

    @Inject
    SimpleViewModelFactory mFactory;
    private static final int URI_REQUEST = 0;
    private PostsViewModel mPostsViewModel;
    private Post post;
    private Uri postImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new Slide(Gravity.BOTTOM));
        setExitTransition(new Slide(Gravity.TOP));
        AppKt.app(this).component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mPostsViewModel = ViewModelProviders.of(this, mFactory).get(PostsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_new_post, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPostsViewModel.getAddPostResult().observe(getViewLifecycleOwner(), result -> {
            if (getView() == null) return;
            Boolean isSuccessful = result.consume();
            if (isSuccessful != null && isSuccessful) {
                NavHostFragment.findNavController(this).navigate(NewPostFragmentDirections.actionNewPostFragmentToPostDetailFragment(post));
                Snackbar.make(getView(), "Post added!", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(getView(), getString(R.string.post_add_err), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.submitPostBtn, R.id.closeBtn, R.id.addMediaBtn})
    public void submit(View v) {
        switch (v.getId()) {
            case R.id.submitPostBtn:
                submitPost();
                break;
            case R.id.closeBtn:
                NavHostFragment.findNavController(this).popBackStack();
                break;
            case R.id.addMediaBtn:
                loadMediaIntent();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == URI_REQUEST && data != null && data.getData() != null) {
            ContentResolver resolver = requireActivity().getContentResolver();
            try (Cursor cursor = resolver.query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    mAddMediaBtn.setImageTintList(null);
                    postImageUri = Uri.parse(cursor.getString(index));
                    Glide.with(this)
                            .load(postImageUri)
                            .apply(RequestOptions.centerInsideTransform())
                            .into(mAddMediaBtn);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void submitPost() {
        post = new Post("", mBody.getText() == null ? "" : mBody.getText().toString());
        mPostsViewModel.addToDb(post, postImageUri);
    }

    private void loadMediaIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Attach Image"), URI_REQUEST);
    }

}
