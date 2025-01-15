import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rickandmortymvvm.R

@Composable
fun CenteredAppBar(
    titleImageRes: Int,
    onSearchPressed: () -> Unit,
    onLogoutPressed: () -> Unit
) {
    TopAppBar(
        backgroundColor = colorResource(id = R.color.soft_blue),
        elevation = 0.dp,
        contentPadding = PaddingValues(horizontal = 8.dp) // Asegura espacio horizontal
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de búsqueda
            IconButton(
                onClick = onSearchPressed,
                modifier = Modifier.size(36.dp) // Tamaño del botón consistente
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = colorResource(id = R.color.dark_green)
                )
            }
            // Espaciador inicial para centrar el contenido
            Spacer(modifier = Modifier.weight(1f))

            // Imagen centrada
            Image(
                painter = painterResource(id = titleImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(150.dp, 50.dp)
            )

            // Espaciador final para centrar
            Spacer(modifier = Modifier.weight(1f))

            // Botón de logout
            IconButton(
                onClick = onLogoutPressed,
                modifier = Modifier.size(36.dp) // Tamaño del botón consistente
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    tint = colorResource(id = R.color.dark_green)
                )
            }
        }
    }
}
