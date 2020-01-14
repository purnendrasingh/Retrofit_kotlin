package com.example.red_retro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter : MoviesAdapter

    lateinit var movies_list : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieAdapter = MoviesAdapter()
        movies_list=findViewById(R.id.rv)

        movies_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        movies_list.adapter = movieAdapter

        movies_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)


        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val apiMovies = retrofit.create(ApiMovies::class.java)

        apiMovies.getMovies()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ movieAdapter.setMovies(it.data) },
                {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    Log.e("Api.movies",it.message)

                })

    }

    inner class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

        private val movies: MutableList<Movie> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            return MovieViewHolder(layoutInflater.inflate(R.layout.item_movie_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            holder.bindModel(movies[position])
        }

        fun setMovies(data: List<Movie>) {
            movies.addAll(data)
            notifyDataSetChanged()
        }

        inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val id : TextView = itemView.findViewById(R.id.name)
            val title : TextView = itemView.findViewById(R.id.real_name)
            val year : TextView = itemView.findViewById(R.id.firstappearance)
            val genre : TextView = itemView.findViewById(R.id.createdby)

            fun bindModel(movie: Movie) {
                id.text =  (movie.id).toString()
                title.text = movie.title
                year.text = movie.year
                genre.text = movie.genre
            }
        }
    }
}
