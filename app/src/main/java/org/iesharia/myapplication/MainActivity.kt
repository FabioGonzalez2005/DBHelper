package org.iesharia.myapplication

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var nameList by remember { mutableStateOf(listOf<String>()) }
    var ageList by remember { mutableStateOf(listOf<String>()) }
    var nameValue by remember { mutableStateOf("") }
    var ageValue by remember { mutableStateOf("") }
    var selectedName by remember { mutableStateOf<String?>(null) }
    var selectedAge by remember { mutableStateOf<String?>(null) }
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
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp
        )

        // Nombre
        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Edad
        OutlinedTextField(
            value = ageValue,
            onValueChange = { ageValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        var bModifier: Modifier = Modifier.padding(4.dp)
        Column {
            Button(
                modifier = bModifier,
                onClick = {
                    val name = nameValue
                    val age = ageValue

                    db.addName(name, age)
                    Toast.makeText(context, "$name adjuntado a la base de datos", Toast.LENGTH_LONG).show()
                    nameValue = ""
                    ageValue = ""
                }
            ) {
                Text(text = "Añadir")
            }

            Button(
                modifier = bModifier,
                onClick = {
                    val name = nameValue
                    val age = ageValue

                    println("Intentando eliminar el registro con nombre: $name y edad: $age")

                    val rowsDeleted = db.deleteName(name, age)

                    if (rowsDeleted > 0) {
                        Toast.makeText(
                            context,
                            "$name eliminado de la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "No se encontró ningún registro con el nombre $name y la edad $age",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    nameValue = ""
                    ageValue = ""
                }
            ) {
                Text(text = "Eliminar")
            }

            // Botón de Mostrar
            Button(
                modifier = bModifier,
                onClick = {
                    val cursor = db.getName()

                    val names = mutableListOf<String>()
                    val ages = mutableListOf<String>()

                    cursor?.let {
                        if (it.moveToFirst()) {
                            do {
                                names.add(it.getString(it.getColumnIndex(DBHelper.NAME_COl)))
                                ages.add(it.getString(it.getColumnIndex(DBHelper.AGE_COL)))
                            } while (it.moveToNext())
                        }
                        it.close()
                    }

                    nameList = names
                    ageList = ages
                }
            ) {
                Text(text = "Mostrar")
            }

            // Botón de Actualizar
            Button(
                modifier = bModifier,
                onClick = {
                    if (selectedName != null && selectedAge != null) {
                        showUpdateMenu = true
                        nameValue = selectedName!!
                        ageValue = selectedAge!!
                    } else {
                        Toast.makeText(context, "Seleccione un usuario de la lista", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Text(text = "Actualizar")
            }
        }

        // Lista de usuarios
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = "Lista de usuarios:",
            fontSize = 16.sp
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(nameList.zip(ageList)) { (name, age) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            selectedName = name
                            selectedAge = age
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name)
                    Text(text = age)
                }
            }
        }

        // Menú de actualización
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
                        val updatedRows = db.updateName(selectedName!!, nameValue, ageValue)
                        if (updatedRows > 0) {
                            Toast.makeText(context, "$selectedName actualizado correctamente", Toast.LENGTH_LONG).show()
                            selectedName = null
                            selectedAge = null
                            nameValue = ""
                            ageValue = ""
                            showUpdateMenu = false
                        } else {
                            Toast.makeText(context, "No se pudo actualizar $selectedName", Toast.LENGTH_LONG).show()
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
