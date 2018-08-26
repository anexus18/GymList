package com.anexus.list

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MusicFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MusicFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MusicFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requestPermission(listener as Context)

//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.generic_cover )  //creating bitmap to make image round
//        val rounded = RoundedBitmapDrawableFactory.create(resources, bitmap)
//        rounded.isCircular = true
//        songImageFront.setImageDrawable(rounded)

//        myQuickSort(songs, 0, songs.size - 1, "title")
//        adapter = SongsAdapter(this, songs) {
//            song ->
//            songTitleFront.text = song.title
//            songArtistFront.text = song.artist
//        }
//
//        //setting the adapter of recylcerView and the song click
//        songsListView.adapter = adapter                             //assigning the adapter
//        val layoutManager =  LinearLayoutManager(this)       //layout manager
//        songsListView.layoutManager = layoutManager
//        songsListView.setHasFixedSize(true)

    }

    private fun requestPermission(context: Context){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        val builder = AlertDialog.Builder(context)
//         2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title_storage)
        builder.setPositiveButton(R.string.ok) { dialog, id ->
            ActivityCompat.requestPermissions(MainActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            requestPermission(context)
        }
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            val dialog = builder.create()
            dialog.show()
        }else
//            retrieveMusic()
            true

    }

//    private fun retrieveMusic(): ArrayList<Song>{
//        val a1: ArrayList<Song> = ArrayList()
//        val contentResolver: ContentResolver = contentResolver
//        val uri= android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val cursor = contentResolver.query(uri, null, null, null, null);
//        if (cursor == null) {
//            // query failed, handle error.
//        } else if (!cursor.moveToFirst()) {
//            // no media on the device
//
//        } else {
//            val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
//            val idColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
//            do {
//                val thisId = cursor.getLong(idColumn);
//                val thisTitle = cursor.getString(titleColumn);
//                a1.add(Song(thisTitle, "", null, 0, thisId))
//                // ...process entry...
//            } while (cursor.moveToNext())
//        }
//        return a1
//    }

    private fun inverti(a: ArrayList<Song>, i: Int, j: Int): Int{
        val tmp = a[i + 1]
        a[i + 1] = a[j]
        a[j] = tmp
        return i + 1
    }

    private fun distribution(a: ArrayList<Song>, sx: Int, dx: Int, orderBy: String): Int {
        val px = dx
        var i: Int = sx - 1
//        var j: Int = sx
        for (j in sx..dx){
            when(orderBy){
                "title" -> if(a[j].title < a[px].title) i = inverti(a, i, j)
                "artist" ->if(a[j].artist < a[px].artist) i = inverti(a, i, j)
                "length" ->if(a[j].length < a[px].length) i = inverti(a, i, j)
            }
        }
        val tmp = a[i + 1]
        a[i + 1] = a[px]
        a[px] = tmp
        return i + 1
    }

    private fun myQuickSort(a: ArrayList<Song>, sx: Int, dx: Int, orderBy: String): Unit{
        if(sx < dx){
            val px = distribution(a, sx, dx, orderBy)
            myQuickSort(a, sx, px - 1, orderBy)
            myQuickSort(a, px + 1, dx, orderBy)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MusicFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}