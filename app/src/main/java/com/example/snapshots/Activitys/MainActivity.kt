package com.example.snapshots.Activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.snapshots.Fragments.AddFragment
import com.example.snapshots.Fragments.HomeFragment
import com.example.snapshots.Fragments.ProfileFragment
import com.example.snapshots.R
import com.example.snapshots.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActivityFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val RC_SINGIN = 21
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private lateinit var auth: FirebaseAuth
    private var customToken: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        auth = Firebase.auth

        setUpAuth()
        setUpBottomBar()
    }
/*    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = mFirebaseAuth?.currentUser
        } else {

        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }*/

/*    private fun setUpAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            Log.i("prueba",user.toString())
            if (user == null){
                Log.i("prueba","entro")
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )).build(), RC_SINGIN)
            }
        }


    }*/



    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
        } else {
        }
    }


    private fun setUpAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            //Si el usuario aun no se ha logueado
            if(user == null) {
                // Create and launch sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)
            }
        }
    }

    private fun setUpBottomBar(){

        val homeFragment = HomeFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()

        mFragmentManager = supportFragmentManager

        mActivityFragment = homeFragment

        mFragmentManager.apply {
            beginTransaction().add(R.id.hostFragment,profileFragment, ProfileFragment::class.java.name)
                .hide(profileFragment).commit()

            beginTransaction().add(R.id.hostFragment,addFragment, AddFragment::class.java.name)
                .hide(addFragment).commit()

            beginTransaction().add(R.id.hostFragment,homeFragment, HomeFragment::class.java.name)
                .commit()
        }

        mBinding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home ->{
                    mFragmentManager.beginTransaction().hide(mActivityFragment).show(homeFragment).commit()
                    mActivityFragment = homeFragment
                    true
                }
                R.id.action_add ->{
                    mFragmentManager.beginTransaction().hide(mActivityFragment).show(addFragment).commit()
                    mActivityFragment = addFragment
                    true
                }
                R.id.action_profile ->{
                    mFragmentManager.beginTransaction().hide(mActivityFragment).show(profileFragment).commit()
                    mActivityFragment = profileFragment
                    true
                }
                else -> false

            }
        }

    }



    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener { mAuthListener }
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener { mAuthListener }
    }

    override fun onStart() {
        super.onStart()
    }

    /*private fun startSignIn() {
        // Initiate sign in with custom token
        // [START sign_in_custom]
        Log.i("prueba","entro")
        customToken?.let {
            Log.i("prueba","entro1")
            auth.signInWithCustomToken(it)
                .addOnCompleteListener(this) { task ->
                    Log.i("prueba","entro2")
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
        // [END sign_in_custom]
    }

    private fun updateUI(user: FirebaseUser?) {
        startSignIn()
    }*/


}