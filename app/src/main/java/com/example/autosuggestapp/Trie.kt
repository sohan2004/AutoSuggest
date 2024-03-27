package com.example.autosuggestapp

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.toLowerCase
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale


class Node{
    val links: Array<Node?> = arrayOfNulls(26)
    var isEnd: Boolean = false
    fun isContains(c: Char): Boolean{
        if(!c.isLetter()||!c.isLowerCase()) return false
        return links[c-'a']!=null
    }
    fun put(c: Char, node: Node){
            links[c-'a']=node
    }
    fun get(c: Char): Node?{
        return links[c-'a']
    }
}


class Trie(context: Context) {
    private val root: Node=Node()
    fun insert(word: String){
        var node: Node= root
        word.forEach {
            c ->
            if(!node.isContains(c)){
                node.put(c, Node())
            }
            node = node.get(c)!!
        }
        node.isEnd=true
    }

    fun search(word: String): Boolean{
        var node:Node=root
        word.forEach {
            c->
            if(!node.isContains(c)) return false
            node=node.get(c)!!
        }
        return node.isEnd
    }
    fun prefixWords(word: String): MutableList<String>{
        var node: Node=root
        val ans: MutableList<String> =mutableListOf<String>()
        word.forEach {
            c->
            if(!node.isContains(c)) return ans
            node=node.get(c)!!
        }
        allPaths(ans, word, node)
        return ans
    }

    fun allPaths(ans: MutableList<String>, word:String, node: Node){
        if(node.isEnd&& word.isNotEmpty()) ans.add(word)
        for (i in 0 until 26){
            if(node.isContains((i+ 'a'.code).toChar())){
                var wor=word
                wor+=(i+ 'a'.code).toChar()
                allPaths(ans, wor, node.get((i+ 'a'.code).toChar())!!)
            }
        }
    }
    init {
        loadWords(context, this)
        println(search("and"))
    }
}
fun loadWords(context: Context, trie:Trie){
    var inputStream: InputStream=context.resources.openRawResource(R.raw.common_words)
    val reader = BufferedReader(InputStreamReader(inputStream))
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        val words = line!!.split("\\s+".toRegex())
       for (word in words) {
           val result = word.replace(Regex("[^a-z]"), "")
            trie.insert(result)
        }
    }
    reader.close()
}


fun main(){
//    val trie: Trie= Trie()
//    trie.insert("apple")
//    trie.insert("app")
//    println(trie.search("and"))
//    var ans=trie.prefixWords("apk")
//    ans.forEach{
//        println(it)
//    }
}