package com.anexus.list


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.anexus.list.adapters.ProgramAdapter
import com.anexus.list.roomDatabase.DbWorkerThread
import com.anexus.list.roomDatabase.ProgramDatabase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GymFragment : Fragment() {

    private var mainView: View? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private val mUiHandler = Handler()
    private var mDatabase: ProgramDatabase? = null
    private lateinit var ladapter: ProgramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        mDatabase = ProgramDatabase.getInstance(this.requireContext())

        val programData: ArrayList<Program> = ArrayList()
        fetchWeatherDataFromDb(programData)
        Data.programs.addAll(programData)

        ladapter = ProgramAdapter(programData){
            val intent = Intent(context, SessionManager::class.java)
            intent.putExtra(PROGRAM_NAME_EXTRA , it.name)

            Data.currentProgram = it
            startActivity(intent)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_gym, container, false)
        mainView?.findViewById<RecyclerView>(R.id.programList)?.apply {
            adapter = ladapter
            layoutManager = LinearLayoutManager(this.context)
        }
        return mainView
    }

    override fun onDestroy() {
        ProgramDatabase.destroyInstance()
        mDbWorkerThread.quit()
        super.onDestroy()
    }



    private fun fetchWeatherDataFromDb(programData: ArrayList<Program>) {
        val task = Runnable {
            val programData2 =
                    mDatabase?.programDao()?.getAll()
            mUiHandler.post {
                if (programData2 == null || programData2.isEmpty()) {
                    Toast.makeText(this.requireContext(), "No data in cache..!!", Toast.LENGTH_SHORT).show()
                }else{

                    Toast.makeText(this.requireContext(), programData2[0].sessions[0].name , Toast.LENGTH_SHORT).show()
                    programData.addAll(programData2)
                    ladapter.notifyDataSetChanged()
                }
            }
        }
        mDbWorkerThread.postTask(task)

    }
}
