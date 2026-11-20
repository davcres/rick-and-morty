package com.davcres.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.davcres.template.core.ui.theme.TemplateTheme
import com.davcres.template.feature.characters.navigation.CharactersNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemplateTheme {
                CharactersNavigation()
            }
        }
    }
}
