package org.iesharia.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesharia.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) { innerPadding ->
                    MainActivity(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MainActivity(modifier: Modifier) {
    val context = LocalContext.current
    val db = DBHelper(context)
    var userList by remember { mutableStateOf(listOf<Triple<Int, String, String>>()) }
    var nameValue by remember { mutableStateOf("") }
    var ageValue by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var showUpdateMenu by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nID/Nombre/Edad",
            fontSize = 10.sp
        )

        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = ageValue,
            onValueChange = { ageValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        val bModifier = Modifier.padding(4.dp)
        Row {
            Column {
                Button(
                    modifier = bModifier,
                    onClick = {
                        db.addUser(nameValue, ageValue)
                        Toast.makeText(
                            context,
                            "$nameValue adjuntado a la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                        nameValue = ""
                        ageValue = ""
                    }
                ) {
                    Text(text = "Añadir")
                }

                Button(
                    modifier = bModifier,
                    onClick = {
                        db.deleteUser(nameValue, ageValue)
                        Toast.makeText(
                            context,
                            "$nameValue eliminado de la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                        nameValue = ""
                        ageValue = ""
                    }
                ) {
                    Text(text = "Eliminar")
                }
            }
            Row {
                Column {
                    Button(
                        modifier = bModifier,
                        onClick = {
                            userList = db.getUsers()
                        }
                    ) {
                        Text(text = "Mostrar")
                    }

                    Button(
                        modifier = bModifier,
                        onClick = {
                            if (selectedId != null) {
                                showUpdateMenu = true
                            } else {
                                Toast.makeText(
                                    context,
                                    "Seleccione un usuario de la lista",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {
                        Text(text = "Actualizar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = "Lista de usuarios:",
            fontSize = 16.sp
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(userList) { (id, name, age) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            selectedId = id
                            nameValue = name
                            ageValue = age
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "$id - $name")
                    Text(text = age)
                }
            }
        }

        if (showUpdateMenu) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Actualizar usuario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                OutlinedTextField(
                    value = nameValue,
                    onValueChange = { nameValue = it },
                    label = { Text("Nuevo Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = ageValue,
                    onValueChange = { ageValue = it },
                    label = { Text("Nueva Edad") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Button(
                    onClick = {
                        selectedId?.let {
                            db.updateUser(it, nameValue, ageValue)
                            Toast.makeText(context, "Usuario actualizado correctamente", Toast.LENGTH_LONG).show()
                            selectedId = null
                            nameValue = ""
                            ageValue = ""
                            showUpdateMenu = false
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Guardar Cambios")
                }
            }
        }
    }
}
