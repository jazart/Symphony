package com.jazart.symphony.featured

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.NonNull
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar

import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.model.Song

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Objects



class UploadDialog : DialogFragment(), DialogInterface.OnClickListener {

   private lateinit var mSongViewModel: SongViewModel
   private lateinit var mArtists: TextInputLayout
   private lateinit var mSongTitle: TextInputLayout
   private lateinit var mUploadProgress: ProgressBar
   private val mSong = Song()

   private val fileSize: String
       get() {
            val resolver = requireActivity().contentResolver
            val projection = arrayOf(MediaStore.Audio.Media.SIZE)
            val cursor = resolver.query(Uri.parse(mSong.uri), projection, null, null, null)
            val fileSize: String

            require(cursor != null && cursor.moveToFirst())
            val size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            fileSize = cursor.getString(size)
            cursor.close()

       return fileSize
   }

    private val artistsFromUi: List<String>
        @NonNull
        get() {
            var result: MutableList<String> = ArrayList()

            if (mArtists.editText?.text.toString().contains(",")) {
                val artists = mArtists.editText?.text.toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                result = artists.toMutableList()
            } else {
                result.add(mArtists.editText?.text.toString())
            }
            return result
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        mSongViewModel = ViewModelProviders.of(this, SimpleViewModelFactory {
            SongViewModel(activity?.application as App)
        }).get(SongViewModel::class.java)
        if (arguments != null) {
            mSong.uri = arguments!!.getString(ARG_URI)
        }
        updateProgress()
    }

    private fun updateProgress() {
        mSongViewModel.percentageLiveData.observe(this, Observer { progress ->
                mUploadProgress.progress = progress
                if (progress == 100) {
                    mSongViewModel.refreshContent()
                    dismiss()
                }
        })
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
            mSong.name = Objects.requireNonNull<EditText>(mSongTitle.editText)?.text.toString()
            mSong.artists = result

            val size = fileSize
            try {
                mSongViewModel.addSongToStorage(mSong, convertSongUriToFile(), size)
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
        mSong.name = mSongTitle.editText?.text.toString()
        mSong.artists = result
        try {
            val size = fileSize
            Log.d("TAG/ UPLOAD", size)
            mSongViewModel.addSongToStorage(mSong, convertSongUriToFile(), size)
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
        const val TAG = "UploadDialog"
        private const val ARG_URI = "1"

        fun newInstance(uri: Uri): UploadDialog {
            val mBundle = Bundle()
            mBundle.putString(ARG_URI, uri.toString())
            val upDia = UploadDialog()
            upDia.arguments = mBundle
            return upDia
        }
    }
}
