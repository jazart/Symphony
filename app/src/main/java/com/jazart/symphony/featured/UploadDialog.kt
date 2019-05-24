package com.jazart.symphony.featured

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.model.Song
import kotlinx.android.synthetic.main.fragment_upload_dialog.view.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.IllegalStateException
import java.util.*


class UploadDialog : DialogFragment(), DialogInterface.OnClickListener {

    private val songViewModel: SongViewModel by viewModels {
        SimpleViewModelFactory {
            SongViewModel(activity?.application as App)
        }
    }
    private val args: UploadDialogArgs by navArgs()
    private val song = Song()
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        song.uri = args.songUri
        updateProgress()
    }

    @SuppressLint("InflateParams")
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_upload_dialog, null)

        dialogView.upload_button.setOnClickListener {
            val result = artistsFromUi()
            song.name = dialogView.enter_song_title.editText?.text.toString()
            song.artists = result

            val size = fileSize()
            try {
                songViewModel.addSongToStorage(song, convertSongUriToFile(), size)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            dialogView.upload_button.visibility = View.GONE
            dialogView.upload_progress.visibility = View.VISIBLE
        }
        return AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Upload")
                .create()

    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            manager.beginTransaction()
                    .add(this, tag)
                    .commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            super.show(manager, tag)
        }
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        val result = artistsFromUi()
        song.name = dialogView.enter_song_title?.editText?.text.toString()
        song.artists = result
        try {
            val size = fileSize()
            Log.d("TAG/ UPLOAD", size)
            songViewModel.addSongToStorage(song, convertSongUriToFile(), size)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    @Throws(FileNotFoundException::class)
    private fun convertSongUriToFile(): FileInputStream {
        val resolver = requireActivity().contentResolver
        return resolver.openInputStream(Uri.parse(song.uri)) as FileInputStream
    }

    private fun fileSize(): String {
        val resolver = requireActivity().contentResolver
        val projection = arrayOf(MediaStore.Audio.Media.SIZE)
        val cursor = resolver.query(Uri.parse(song.uri), projection, null, null, null)
        val fileSize: String

        require(cursor != null && cursor.moveToFirst())
        val size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
        fileSize = cursor.getString(size)
        cursor.close()

        return fileSize
    }

    private fun artistsFromUi(): List<String> {
        var result: MutableList<String> = ArrayList()

        if (dialogView.enter_artists.editText?.text.toString().contains(",")) {
            val artists = dialogView.enter_artists.editText?.text.toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            result = artists.toMutableList()
        } else {
            result.add(dialogView.enter_artists.editText?.text.toString())
        }
        return result
    }

    private fun updateProgress() {
        songViewModel.percentageLiveData.observe(this, Observer { progress ->
            dialogView.upload_progress?.progress = progress
            if (progress == 100) {
                songViewModel.refreshContent()
                dismiss()
            }
        })
    }

    private fun inject() {
        val app = requireActivity().application as App
        app.component.inject(this)
    }

    companion object {
        const val TAG = "UploadDialog"
        private const val ARG_URI = "1"

        @JvmStatic
        fun newInstance(uri: Uri): UploadDialog {
            val bundle = Bundle()
            bundle.putString(ARG_URI, uri.toString())
            val uploadDialog = UploadDialog()
            uploadDialog.arguments = bundle
            return uploadDialog
        }
    }
}
