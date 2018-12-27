package com.jazart.symphony.featured

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ContentResolver
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar

import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.model.Song

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Arrays
import java.util.Objects

import javax.inject.Inject


class UploadDialog : DialogFragment(), DialogInterface.OnClickListener {

    private var mSongViewModel: SongViewModel? = null
    private var mArtists: TextInputLayout? = null
    private var mSongTitle: TextInputLayout? = null
    private var mUploadProgress: ProgressBar? = null
    private val mSong = Song()

    private val fileSize: String
        get() {
            val resolver = requireActivity().contentResolver
            val projection = arrayOf(MediaStore.Audio.Media.SIZE)
            val cursor = resolver.query(Uri.parse(mSong.uri), projection, null, null, null)
            var fileSize = ""

            assert(cursor != null)
            if (cursor!!.moveToFirst()) {
                val size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                fileSize = cursor.getString(size)
                cursor.close()
            }
            return fileSize
        }

    private val artistsFromUi: List<String>
        @NonNull
        get() {
            var result: MutableList<String> = ArrayList()

            if (Objects.requireNonNull<EditText>(mArtists!!.editText).getText().toString().contains(",")) {
                val artists = mArtists!!.editText.getText().toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                result = Arrays.asList(*artists)
            } else {
                result.add(mArtists!!.editText.getText().toString())
            }
            return result
        }

    override fun onCreate(@Nullable savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        inject()
        mSongViewModel = ViewModelProviders.of(this).get(SongViewModel::class.java)
        if (arguments != null) {
            mSong.uri = arguments.getString(ARG_URI)
        }
        updateProgress()
    }

    private fun updateProgress() {
        //        mSongViewModel.getPercentageLiveData().observe(this, new Observer<Integer>() {
        //            @Override
        //            public void onChanged(@Nullable Integer progress) {
        //                mUploadProgress.setVisibility(View.VISIBLE);
        //                mUploadProgress.setProgress(progress, true);
        //                if (progress == 100) dismiss();
        //            }
        //        });
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_upload_dialog, null)

        mArtists = view.findViewById(R.id.enter_artists)
        mSongTitle = view.findViewById(R.id.enter_song_title)
        mUploadProgress = view.findViewById(R.id.upload_progress)
        view.findViewById<View>(R.id.upload_button).setOnClickListener {
            val result = artistsFromUi
            mSong.name = Objects.requireNonNull<EditText>(mSongTitle!!.editText).getText().toString()
            mSong.artists = result

            val size = fileSize
            try {
                mSongViewModel!!.addSongToStorage(mSong, convertSongUriToFile(), size)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        return AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Upload")
                .create()

    }

    override fun show(manager: FragmentManager, tag: String) {
        manager.beginTransaction()
                .add(this, tag)
                .commitAllowingStateLoss()
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        val result = artistsFromUi
        mSong.name = Objects.requireNonNull<EditText>(mSongTitle!!.editText).getText().toString()
        mSong.artists = result
        try {
            val size = fileSize
            Log.d("TAG/ UPLOAD", size)
            mSongViewModel!!.addSongToStorage(mSong, convertSongUriToFile(), size)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    @Throws(FileNotFoundException::class)
    private fun convertSongUriToFile(): FileInputStream {
        val resolver = requireActivity().contentResolver
        return resolver.openInputStream(Uri.parse(mSong.uri)) as FileInputStream
    }

    private fun inject() {
        val app = requireActivity().application as App
        app.component.inject(this)
    }

    companion object {
        val TAG = "UploadDialog"
        private val ARG_URI = "1"

        fun newInstance(uri: Uri): UploadDialog {
            val mBundle = Bundle()
            mBundle.putString(ARG_URI, uri.toString())
            val upDia = UploadDialog()
            upDia.arguments = mBundle
            return upDia
        }
    }
}
