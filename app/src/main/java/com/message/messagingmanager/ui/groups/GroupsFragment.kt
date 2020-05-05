package com.message.messagingmanager.ui.groups

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.activity.CreateGroupActivity

class GroupsFragment() : Fragment() {

    private lateinit var groupsViewModel: GroupsViewModel
    private var recyclerView: RecyclerView? = null

    private lateinit var databaseReferenceGroup: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String

    internal lateinit var groupsList: ArrayList<Group>

    internal lateinit var view: View

    private var textVal: String = ""

    companion object{
        @SuppressLint("StaticFieldLeak")
        var activiy: Activity = Activity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        view = inflater.inflate(R.layout.fragment_groups, container, false)
        recyclerView = view.findViewById(R.id.recyclerGroup)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        databaseReferenceGroup = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups")
        databaseReferenceGroup.keepSynced(true)
        groupsList = ArrayList()

        val floatingActionButtonGroups = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonGroups)
        floatingActionButtonGroups.setOnClickListener {
            startActivity(Intent(activity, CreateGroupActivity::class.java))
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        databaseReferenceGroup.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                groupsList.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val group = msgSnapshot.getValue(Group::class.java)

                    if (group!!.getUserID() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        groupsList.add(group)
                    }

//                    setAdapter()
                }

                if (groupsList.size <= 0) {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentGroup).visibility = View.VISIBLE
                    view.refreshDrawableState()
                } else {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentGroup).visibility = View.GONE
                    setAdapter()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {

        val manager = LinearLayoutManager(activiy)
        recyclerView!!.layoutManager = manager
        groupsViewModel = GroupsViewModel(activiy, groupsList)
        recyclerView!!.adapter = groupsViewModel
        setDataSource()
    }

    private fun setDataSource() {
        groupsViewModel.setDataToAdapter(groupsList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem: MenuItem = menu.findItem(R.id.item_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        // to remove search icon from keyboard because we just on locale recyclerview not online recyclerview
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        // to set search text
        searchView.queryHint = getText(R.string.searchGroup)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                groupsViewModel.filter.filter(newText)
                if (newText != null) {
                    textVal = newText
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                groupsViewModel.filter.filter(textVal)
                // change color
                val id = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
                val editText = searchView.findViewById<View>(id) as EditText
                editText.setHintTextColor(Color.WHITE)
//                editText.setText("")
//                adapter.filter.filter("")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                groupsViewModel.filter.filter("")
                return true
            }

        })
    }
}