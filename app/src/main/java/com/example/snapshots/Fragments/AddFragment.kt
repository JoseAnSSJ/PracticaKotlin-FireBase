package com.example.snapshots.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snapshots.Data.SnapShot
import com.example.snapshots.R
import com.example.snapshots.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.ref.PhantomReference

class AddFragment : Fragment() {

    private lateinit var mBinding: FragmentAddBinding
    private lateinit var mStorageReference: StorageReference
    private lateinit var mDataBaseReference: DatabaseReference
    private val RC_GALLERY: Int = 18
    private val PATH_SNAPSHOT = "SnapShots"
    private var mPhotoSelectUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       mBinding = FragmentAddBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.btnPost.setOnClickListener {
            postSnapShot()
        }

        mBinding.btnSelect.setOnClickListener{
            openGalery()
        }

        mStorageReference = FirebaseStorage.getInstance().reference
        mDataBaseReference = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOT)
    }

    private fun openGalery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_GALLERY)
    }

    private fun postSnapShot() {

        mBinding.progresBar.visibility = View.VISIBLE
        val key = mDataBaseReference.push().key
        val storageReference = mStorageReference.child(PATH_SNAPSHOT).child("MY_PHOTO")
        if(mPhotoSelectUri != null){
            storageReference.putFile(mPhotoSelectUri!!).addOnProgressListener {
                val progress = (100 * it.bytesTransferred/it.totalByteCount).toDouble()
                mBinding.progresBar.progress = progress.toInt()
                mBinding.txtMenssage.text = "$progress %"
            }
                .addOnCompleteListener{
                    mBinding.progresBar.visibility = View.INVISIBLE
                }
                .addOnSuccessListener { onSucces ->
                    onSucces.storage.downloadUrl.addOnSuccessListener {
                        saveSnapshot(key!!,it.toString(),mBinding.edTitle.text.toString().trim())
                        mBinding.tiTitle.visibility = View.GONE
                        mBinding.txtMenssage.text = getString(R.string.post_masage_title)
                    }
                    Snackbar.make(mBinding.root, getString(R.string.message_post), Snackbar.LENGTH_LONG).show()
                }
                .addOnFailureListener{
                    Snackbar.make(mBinding.root, getString(R.string.message_error_post), Snackbar.LENGTH_LONG).show()
                }
        }


    }

    private fun saveSnapshot(key:String, url:String, title:String){
        val snapshot = SnapShot(
            title = title,
            photoUrl = url
        )

        mDataBaseReference.child(key).setValue(snapshot)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == RC_GALLERY){
                 mPhotoSelectUri = data?.data
                mBinding.imgPhoto.setImageURI(mPhotoSelectUri)
                mBinding.tiTitle.visibility = View.VISIBLE
                mBinding.txtMenssage.text = getString(R.string.post_masage_valid_title)
            }

        }
    }


}