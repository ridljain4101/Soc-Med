
package com.example.socmed


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socmed.models.Post
import com.example.socmed.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_posts.*


private const val TAG = "PostsActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {


    private var signedInUser : User? = null
    private lateinit var firestoreDb : FirebaseFirestore

    // it is lateinit since we just initialize it then it is unknown
    private lateinit var posts : MutableList<Post>
    private lateinit var adapter: PostsAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)



        // create layout file which represents one post-done
        // create data source-done
        posts= mutableListOf()
        //create adapter
        adapter= PostsAdapter(this,posts)// context and data source
        // bind adapter and layout manager to the rv

        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        firestoreDb = FirebaseFirestore.getInstance()
        // accessing cloud firebase from this actiivty

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            // llook at the signed in user uid and use that to query on users collection
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }





        var postsReference = firestoreDb
            .collection("posts")
            // power of firebase
            // we can limit the number of posts
            // even if we have a lot of them and what we require is less
            .limit(20)
            // here ordering my virtue of creation time
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null){ // means we are looking at the profile
            supportActionBar?.title = username // this displays the username on the menu at the top to show whose profile it is
            postsReference =  postsReference.whereEqualTo("user.username",username) // so we get posts which has the same username


        }
        postsReference.addSnapshotListener{snapshot,exception ->
            // snapshot listener will enable us to know if there is
            // any change in the collection to be known
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exeception when querying posts", exception)
                return@addSnapshotListener
                // RETURNING EARLY AS SOMETHING IS WRONG
            }
            val postList = snapshot.toObjects(Post::class.java)

            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()

            for(post in postList) {
                Log.i(com.example.socmed.TAG, "Post $post")
            }
        }


  

// TODO : make a query to Firestore to retrieve data
    }

    // below two are used for referencing the menu_posts icon inside of postsactivity

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_profile){ // this is to clear that the user taps the item on the menu which has the
            // same id as we mentioned for the
            // icon
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username) // HERE this is used so that we are able to see only the posts by the profile owner
            //by tapping on the profile
            startActivity(intent)  }
        return super.onOptionsItemSelected(item)





// question mark used when a particular var can be null(ktn syntax)
    }}

