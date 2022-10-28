package com.example.snapshots.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.snapshots.Data.SnapShot
import com.example.snapshots.R
import com.example.snapshots.databinding.FragmentHomeBinding
import com.example.snapshots.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {
    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mFireBaseAdapter: FirebaseRecyclerAdapter<SnapShot,SnapShotHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = FirebaseDatabase.getInstance().reference.child("SnapShots")
        val option = FirebaseRecyclerOptions.Builder<SnapShot>().setQuery(query, SnapShot::class.java).build()

        mFireBaseAdapter = object  : FirebaseRecyclerAdapter<SnapShot, SnapShotHolder>(option){
            private lateinit var mContext: Context
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapShotHolder {
                mContext = parent.context

                val view = LayoutInflater.from(mContext).inflate(R.layout.item_snapshot, parent, false)

                return SnapShotHolder(view)

            }

            override fun onBindViewHolder(holder: SnapShotHolder, position: Int, model: SnapShot) {
                val snapshot = getItem(position)

                with(holder){
                    setListener(snapshot)
                    binding.txtTitle.text = snapshot.title
                    Glide.with(mContext).load(snapshot.photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(binding.imgPhoto)
                }

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progresBar.visibility = View.GONE
                notifyDataSetChanged()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(mContext, error.message, Toast.LENGTH_LONG).show()
            }

        }

        mLayoutManager = LinearLayoutManager(context)
        mBinding.recliclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFireBaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mFireBaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFireBaseAdapter.stopListening()
    }

    inner class SnapShotHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemSnapshotBinding.bind(view)


        fun setListener(snapShot: SnapShot){

        }
    }

}