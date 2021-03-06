package com.anexus.list

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import com.anexus.list.Data.CHANNEL_ID
import com.anexus.list.roomDatabase.ProgramDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.program_name_dialog.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get the database
        Data.programDb = ProgramDatabase.getInstance(this)!!.programDao()

        setFragment(R.id.navigationViewProgram)


        //OLD (for exercises written to file)
        //checks for data written
//        if(File(filesDir.toString() + "/" + EX_DATA_FILENAME).exists()) {
//            val inputStream = openFileInput(EX_DATA_FILENAME)
//            val inputStreamReader =  InputStreamReader(inputStream)
//            val  bufferedReader =  BufferedReader(inputStreamReader)
//            val string: String? = bufferedReader.readLine()
//            copyDataLL(string)
//            inputStream.close()
//            inputStreamReader.close()
//            bufferedReader.close()
//        }

        setSupportActionBar(findViewById(R.id.mainActTB))
        supportActionBar?.title = getString(R.string.app_name)


        addProgramFab.setOnClickListener {
            //if addProgram button is pressed -> open dialog to insert name then start a new activity
            val nameDialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.program_name_dialog, null)
            nameDialog.setView(view)

            //used to force keyboard to show
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            nameDialog.setPositiveButton(R.string.ok){ _: DialogInterface, _: Int ->
                //remove keyboard
                imm.toggleSoftInputFromWindow(view.windowToken, 0,0)

                //collect the new program
                val newProgram = Program(null, view.programNameEt.text.toString(), ArrayList())
                Data.currentProgram = newProgram

                insertDataInDb(newProgram)
//                mDatabase.programDao().insert(Program( view.programNameEt.text.toString()))

                //start new Activity
                val intent = Intent(this, SessionManager::class.java)
                intent.putExtra(PROGRAM_NAME_EXTRA ,view.programNameEt.text.toString())
                startActivity(intent)
            }

            //cancel action
            nameDialog.setNegativeButton(R.string.cancel){ dialogInterface: DialogInterface, _: Int ->
                imm.toggleSoftInputFromWindow(view.windowToken, 0,0)
                dialogInterface.cancel()
            }

            nameDialog.show()

            //Highlight the default text
            view.programNameEt.requestFocus()
            view.programNameEt.selectAll()
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener{
            if(it.itemId == R.id.navigationViewMusic){
                if(storagePermissionGranted()){
////                }else{
//                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
                    val snackbar = Snackbar.make(mainActivityCoordinatorLayout, "This section requires storage access", Snackbar.LENGTH_SHORT)
                    snackbar.anchorView = addProgramFab
                    snackbar.show()
                }
                return@setOnNavigationItemSelectedListener setFragment(it.itemId)

//                    Toast.makeText(this, "textttt", Toast.LENGTH_LONG).show()
//                    return@setOnNavigationItemSelectedListener true
//                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

        private fun storagePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title_storage)
                builder.setPositiveButton(R.string.ok) { _, _ ->
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)}
                builder.create().show()

                return false
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        0)

                return false
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return true
        }

    }

    private fun setFragment(itemId: Int): Boolean{
        when(itemId) {
            R.id.navigationViewProgram -> {
                musicCardView?.visibility = View.GONE
                addProgramFab.show()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainActFragment, GymFragment())
                        .commit()
                return true
            }

            R.id.navigationViewMusic -> {
                musicCardView?.visibility = View.VISIBLE
                addProgramFab.hide()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainActFragment, MusicFragment())
                        .commit()
                return true
            }
            R.id.navigationViewStart -> {
                musicCardView?.visibility = View.GONE
                addProgramFab.hide()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainActFragment, TimerFragment())
                        .commit()
                return true
            }
            else -> return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }

//        R.id.action_favorite -> {
//            // User chose the "Favorite" action, mark the current item
//            // as a favorite...
//            true
//        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun insertDataInDb(program: Program) {
        //OLD
//        val task = Runnable { mDatabase?.programDao()?.insert(program) }
//        mDbWorkerThread.postTask(task)

        GlobalScope.launch { Data.programDb.insert(program) }
    }

//    private fun copyDataLL(dataString: String?) {
//        var i = 0
//        if(dataString == null)
//            Log.e("idk", "è nullo, boh")
//        else {
//            Data.exList.removeAll(Data.exList)
//            while (i < dataString.length - 1) {
//                if ((dataString[i]) == '=') {
//                    var x = dataString.indexOf(',', i)
//                    val name = dataString.substring(i + 1, x)
//                    i = x + 7
//                    x = dataString.indexOf(',', i)
//                    val sets = dataString.substring(i, x)
//                    i = x + 7
//                    x = dataString.indexOf(',', i)
//                    val reps = dataString.substring(i, x)
//                    i = x + 7
//                    x = dataString.indexOf(')', i)
//                    val rest = dataString.substring(i, x)
//                    Data.exList.add(Ex(name, sets.toInt(), reps.toInt(), rest.toInt()))
//
//                }
//                i++
//            }
//        }
//    }



    override fun onDestroy() {
//        mDbWorkerThread.quit()
        this.openFileOutput(EX_DATA_FILENAME, Context.MODE_PRIVATE).use {
            it.write(Data.exercises.toString().toByteArray())
        }
        super.onDestroy()
    }
}
