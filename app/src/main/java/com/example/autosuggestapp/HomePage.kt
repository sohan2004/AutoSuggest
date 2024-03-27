package com.example.autosuggestapp
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(trie: Trie){
    var searchString by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var words by remember {
        mutableStateOf(listOf<String>())
    }
    var isClicked by remember {
        mutableStateOf(false)
    }
    var context = LocalContext.current
    var isDialog by remember {
        mutableStateOf(false)
    }
    var isDialogSearch by remember {
        mutableStateOf(false)
    }
    var isFocused by remember {
        mutableStateOf(false)
    }
    var autoWords by remember {
        mutableStateOf(listOf<String>())
    }
    var foundPrefix by remember {
        mutableStateOf("")
    }
    //autoWords=words.shuffled().take(5)
    var suggestionStay by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = { Text(text = "AutoSuggest", color = Color.Black) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),

            actions = {
                IconButton(
                    onClick = {
                        isDialog=true
                        isFocused=false
                    },
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
                IconButton(onClick = {
                    isDialogSearch=true
                    isFocused=false
                },
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            }
        )
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Row (
                    //modifier=Modifier.padding(16.dp)
                ){
                    TextField(value = searchString,
                        modifier=Modifier
                            .fillMaxWidth(),
                        onValueChange = {
                            searchString=it
                            autoWords = if(searchString.text.length>0&&!Regex("[^a-z]").containsMatchIn(searchString.text))
                                trie.prefixWords(searchString.text)
                            else{
                                emptyList()
                            }
                        },
                        placeholder ={
                            Text(text = "Prefix Search")
                        },

                        trailingIcon = {
                            IconButton(onClick = {
                                //println(trie.search(searchString))
                                words = if(!Regex("[^a-z]").containsMatchIn(searchString.text))
                                    trie.prefixWords(searchString.text)
                                else{
                                    emptyList()
                                }
                                autoWords= emptyList()
                                isClicked=true
                                isFocused=true
                                foundPrefix=searchString.text
                            },
                            ) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            }
                        }
                    )
                }
                Box {
                    if(words.isEmpty()&&isClicked&&isFocused){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(2.dp, Color.Red),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            ),

                            ){
                            Row (
                                modifier = Modifier.padding(8.dp)
                            ){
                                Text(text = "No words found with Prefix ")
                                Text(text = foundPrefix, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                            }
                        }
                    }else{
                        Column {
                            if(isClicked&&!words.isEmpty()) {
                                Row (
                                    modifier = Modifier.padding(8.dp)
                                ){
                                    Text(text = words.size.toString()+" words found with Prefix ")
                                    Text(text = foundPrefix, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                                }

                            }
                            LazyColumn(){
                                if(words.isNotEmpty()){
                                    words.shuffled()
                                    items(words){
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White,
                                                contentColor = Color.Black
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 2.dp
                                            ),

                                            ){
                                            Text(text = it, modifier = Modifier.padding(8.dp))
                                        }
                                    }
                                }
                            }
                        }

                    }
                    //if(suggestionStay){
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 32.dp
                            )
                        ) {
                            LazyColumn(){
                                items(autoWords.shuffled().take(5)){
                                        words->
                                    Text(
                                        text = words,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                //suggestionStay=true
                                                searchString = TextFieldValue(
                                                    text = words,
                                                    selection = TextRange(words.length)
                                                )

                                                //autoWords = emptyList()


                                            },
                                        style = TextStyle(
                                            fontSize = 16.sp
                                        )

                                    )
                                }
                            }
                        }
                    //}

                }
            }
        }



    }

    var addTextField by remember {
        mutableStateOf("")
    }
    var isError by remember{ mutableStateOf(false) }
    var emptyError by remember {
        mutableStateOf(false)
    }
    if(addTextField.isNotBlank()) emptyError=false
    var wordSizeExeeded by remember {
        mutableStateOf(false)
    }
    wordSizeExeeded=addTextField.length>45
    if(isDialog){
        AlertDialog(
            modifier = Modifier.border(width = 5.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp)),
            onDismissRequest = {  isDialog=false
                               emptyError=false},
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),

                    //horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if(addTextField.isBlank()) emptyError=true
                            else{
                                wordSizeExeeded=false
                                isDialog=false
                                trie.insert(addTextField)
                                addTextField=""
                                val toast = Toast.makeText(context, "Word Added", Toast.LENGTH_SHORT)
                                toast.setGravity(Gravity.AXIS_PULL_AFTER, 100, 100) // Set position and offsets
                                toast.show()
                            }

                        },
                        modifier = Modifier.weight(2f),
                        enabled = (!isError&&!wordSizeExeeded)
                    ) {
                        Text(text = "Add")
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Button(
                        onClick = {
                            isDialog=false
                            addTextField=""
                            emptyError=false
                            wordSizeExeeded=false

                        },
                        modifier = Modifier.weight(2f),
                        enabled = true
                    ) {
                        Text(text = "Cancel")
                    }

                } },
            title = { Text(text = "Add Word") },
            text = {
                Column {
                    OutlinedTextField(
                        value = addTextField,
                        onValueChange = {addTextField=it},
                        singleLine = true,
                        label = { Text(text = "Enter") },
                        isError=isError||emptyError||wordSizeExeeded,
                        colors = OutlinedTextFieldDefaults.colors(

                            //focusedBorderColor = Color.Magenta, // Color of the border when focused
                            //unfocusedBorderColor = Color.Black, // Color of the border when not focused
                            errorBorderColor = Color.Red,
                            errorTextColor = Color.Red,
                            errorLabelColor = Color.Red
                        ),
                    )
                    isError= Regex("[^a-z]").containsMatchIn(addTextField)

                    Spacer(modifier = Modifier.height(8.dp))
                    if ( isError) Text(text = "Enter Lower Case English Alphabets Only", color= Color.Red)
                    if(emptyError) Text(text = "Input is Empty", color= Color.Red)
                    if(wordSizeExeeded) Text(text = "Word is too Long\nThe Longest English Word is\npneumonoultramicroscopicsilicovolcanoconiosis", color= Color.Red)

                }
            }
        )
    }



    var searchTextField by remember {
        mutableStateOf("")
    }
    var isThere: Boolean by remember {
        mutableStateOf(false)
    }
    var searchAlert by remember {
        mutableStateOf(false)
    }
    var emptyErrorSearch by remember {
        mutableStateOf(false)
    }
    var presentString by remember {
        mutableStateOf("")
    }
   //emptyErrorSearch=searchTextField.isEmpty()
    if(isDialogSearch){
        AlertDialog(
            modifier = Modifier.border(width = 5.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp)),
            onDismissRequest = {  isDialogSearch=false
                               emptyErrorSearch=false},
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),

                    //horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if(searchTextField.isBlank()){
                                emptyErrorSearch=true
                            }else{
                                emptyErrorSearch=false
                                isThere=trie.search(searchTextField)
                                isDialogSearch=false
                                searchTextField=""
                                searchAlert=true
                            }
                        },
                        modifier = Modifier.weight(2f),
                        enabled = (!isError)
                    ) {
                        Text(text = "Search")
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Button(
                        onClick = {
                            isDialogSearch=false
                            searchTextField=""
                            emptyErrorSearch=false

                        },
                        modifier = Modifier.weight(2f),
                        enabled = true
                    ) {
                        Text(text = "Cancel")
                    }

                } },
            title = { Text(text = "Search Word") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchTextField,
                        onValueChange = {searchTextField=it
                            presentString=it},
                        singleLine = true,
                        label = { Text(text = "Enter") },
                        isError=emptyErrorSearch,
                        colors = OutlinedTextFieldDefaults.colors(

                            //focusedBorderColor = Color.Magenta, // Color of the border when focused
                            //unfocusedBorderColor = Color.Black, // Color of the border when not focused
                            errorBorderColor = Color.Red,
                            errorTextColor = Color.Red,
                            errorLabelColor = Color.Red
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    if(emptyErrorSearch) Text(text = "Input is Empty", color= Color.Red)

                }
            }
        )
    }
    var borderColor by remember {
        mutableStateOf(Color.Black)
    }
    if(searchAlert){
        AlertDialog(
            onDismissRequest = { searchAlert=false },
            modifier = Modifier
                .border(2.dp, borderColor)
                .background(Color.White)
                .padding(16.dp)
            ) {

                if(isThere){
                    borderColor= Color.Green
                    Row(
                    ) {
                        Text(text = presentString, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = " is present in the List")
                    }
                }
                else{
                    borderColor= Color.Red
                    Row(
                    ) {
                        Text(text = presentString, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = " is Not present in the List",
                        )
                    }
                }

        }
    }

}