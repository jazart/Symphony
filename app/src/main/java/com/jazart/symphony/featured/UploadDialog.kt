package com.jazart.symphony.featured

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull
import androidx.core.os.postDelayed
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.di.SimpleViewModelFactory
import entities.Song
import kotlinx.android.synthetic.main.fragment_upload_dialog.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject


class UploadDialog : DialogFragment() {

    @Inject
    lateinit var factory: SimpleViewModelFactory
    private val songViewModel: SongViewModel by viewModels {
        factory
    }
    private val args: UploadDialogArgs by navArgs()
    private val song = Song()
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        song.uri = buildFileUri(args.songUri).toString()
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

            try {
                songViewModel.addSong(song)
                Handler().postDelayed(3000L, null) {
                    dismiss()
                }
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

    private fun buildFileUri(uri: Uri): Uri {
        val resolver = requireActivity().contentResolver
        val inputStream = resolver.openInputStream(uri) ?: return Uri.EMPTY
        val blob = ByteArray(1024 * 8)
        val file = File(requireContext().filesDir.absolutePath + "/test.mp3")
        val output = FileOutputStream(file)
        var bytes = inputStream.read(blob)
        while (bytes != -1) {
            output.write(blob, 0, bytes)
            bytes = inputStream.read(blob)
        }
        inputStream.close()
        return Uri.fromFile(file)
    }

    private fun inject() {
        val app = requireActivity().application as App
        app.component.inject(this)
    }
}
