package com.example.utopia_campsite.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.utopia_campsite.owner.CampsiteRepository
import com.example.utopia_campsite.owner.CampsiteRepository.CampsiteImage

/**
 * Shows one uploaded campsite image with its campsite name and a delete button.
 *
 * @param image: the image and the campsite it belongs to
 * @param onDelete: called when the admin taps Delete (does not delete by itself)
 * @param modifier: optional modifier from the caller
 */
@Composable
fun AdminImageCard(
    image: CampsiteRepository.CampsiteImage,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
){
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = modifier
    ){
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = image.photoUrl,
                contentDescription = "Photo for ${image.campsiteName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(image.campsiteName, fontSize = 14.sp, maxLines = 1)
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Delete image", fontSize = 12.sp)
            }
        }
    }
}
//End AdminImageCard

@Composable
fun AdminImageScreen(
    repo: CampsiteRepository = remember { CampsiteRepository() }
){
    var images by remember { mutableStateOf<List<CampsiteImage>>(emptyList()) }
    var loading by remember {mutableStateOf(true)}
    var errorMessage by remember { mutableStateOf<String?>(null)}

    var deletingId by remember { mutableStateOf<String?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    fun load(){
        loading = true
        errorMessage = null
        repo.getCampsiteImages(
            onSuccess = {images = it; loading = false},
            onFailure = {errorMessage = "Coudln't load images. ${it.message}";loading = false}
        )
    }

    fun deleteImage(image: CampsiteImage){
        if (deletingId!=null) return
        deletingId = image.campsiteId
        deleteError = null
        repo.deleteCampsiteImage(
            campsiteId = image.campsiteId,
            onSuccess = {
                images = images.filter {it.campsiteId != image.campsiteId}
                deletingId = null
            },
            onFailure = {
                deleteError = "Couldn't delete image: ${it.message}"
            }
        )
    }
    LaunchedEffect(Unit) { load() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Campsite Images",
            fontSize =  24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        when{
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }

            errorMessage != null -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { load() }) { Text("Retry") }
            }

            images.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No images have been uploaded.")
            }

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(images, key={it.campsiteId}) {image ->
                    AdminImageCard(
                        image = image,
                        onDelete = { deleteImage(image) }
                    )
                }
            }
        }
    }

}