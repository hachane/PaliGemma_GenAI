package com.example.amobileappfordisabledpeople.ui.views

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.amobileappfordisabledpeople.Data.RequestModel
import com.example.amobileappfordisabledpeople.Data.CoordinatesModelRepoImpl
import com.example.amobileappfordisabledpeople.ui.navigation.ExploreDestination
import com.example.amobileappfordisabledpeople.ui.views.ObjectDetectionUiData
import com.example.amobileappfordisabledpeople.ui.views.UiState
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.example.amobileappfordisabledpeople.AppBar
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.layout.positionInRoot


@Composable
fun ExploreScreen(
    navigateToDangerWarning: () -> Unit = {},
    navigateToDetection: () -> Unit = {}
) {
    val context = LocalContext.current

    var imageHeight by remember { mutableIntStateOf(0) }
    var imageWidth by remember { mutableIntStateOf(0) }

    val viewModel = viewModel<CoordinatesModelViewModel>(
        factory = CoordinatesModelViewModelFactory(
            coordinatesModelRepo = CoordinatesModelRepoImpl(
                applicationContext = context.applicationContext
            )
        )
    )
    val uiState = viewModel.uiState

    var cameraImageFile: File? = context.createImageFile()
    var cameraUri: Uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        cameraImageFile!!
    )

    var galleryImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                galleryImageUri = null
                cameraImageUri = cameraUri
                viewModel.resetData()
            }
        }

    var textPrompt by rememberSaveable { mutableStateOf("") }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.resetData()
            cameraImageUri = null
            galleryImageUri = it
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = uiState) {
        if (uiState is UiState.Error) {
            scope.launch {
                snackbarHostState.showSnackbar(uiState.e)
            }
        }
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                if (dragAmount < 0) {
                    navigateToDetection()
                } else {
                    navigateToDangerWarning()
                }
            }
        },
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            AppBar(destinationName = stringResource(ExploreDestination.titleRes))
        }
    ) { it ->
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (galleryImageUri != null || cameraImageUri != null) {
                Log.d("ImageURI", "Gallery Image URI: $galleryImageUri")
                Log.d("ImageURI", "Camera Image URI: $cameraImageUri")

                ImageWithBoundingBox(
                    uri = galleryImageUri ?: cameraImageUri!!,
                    objectDetectionUiData = (uiState as? UiState.ObjectDetectionResponse)?.result,
                ) { h, w, leftDistance ->
                    imageHeight = h
                    imageWidth = w
                    viewModel.imageLeftDistance = leftDistance
                }
            }

            if (uiState is UiState.Loading) {
                CircularProgressIndicator(color = Color(0xFF29B6F6))
            } else {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            cameraLauncher.launch(cameraUri)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(all = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF29B6F6),
                            contentColor = Color(0xFFFFFFFF)
                        )
                    ) {
                        Text("Open Camera")
                    }
                }

                OutlinedTextField(
                    value = textPrompt,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF29B6F6),
                        unfocusedBorderColor = Color(0xFF29B6F6),
                        focusedLabelColor = Color(0xFF29B6F6),
                        unfocusedLabelColor = Color(0xFF29B6F6),
                        focusedPlaceholderColor = Color(0xFFF5F5F5),
                        unfocusedPlaceholderColor = Color(0xFFF5F5F5),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    label = { Text("Prompt") },
                    onValueChange = { textPrompt = it },
                    placeholder = { Text("Enter text prompt") },
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    trailingIcon = if (textPrompt.isNotEmpty()) {
                        {
                            IconButton(onClick = { textPrompt = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        null
                    }
                )

                Button(
                    onClick = {
                        viewModel.getCoordinatesModel(
                            requestModel = RequestModel(
                                text = textPrompt,
                                uri = galleryImageUri ?: cameraImageUri ?: Uri.EMPTY,
                                height = imageHeight.toString(),
                                width = imageWidth.toString()
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF29B6F6),
                        contentColor = Color(0xFFFAFAFA)
                    )
                ) {
                    Text("Submit")
                }

                if (uiState is UiState.CaptionResponse) {
                    DrawCaptionResponse(uiState.result)
                }

            }
        }
    }
}

@Composable
private fun ImageWithBoundingBox(
    uri: Uri,
    objectDetectionUiData: List<ObjectDetectionUiData>?,
    onSizeChange: (Int, Int, Float) -> Unit
) {
    Box {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .build(),
                modifier = Modifier
                    .heightIn(max = 450.dp)
                    .onGloballyPositioned {
                        onSizeChange(it.size.height, it.size.width, it.positionInRoot().x)
                    },
                contentDescription = null
            )
        }

        objectDetectionUiData?.let {
            DrawObjectDetectionResponse(results = objectDetectionUiData)
        }
    }
}

@Composable
private fun DrawObjectDetectionResponse(results: List<ObjectDetectionUiData>) {
    //initial height set at 0.dp
    val textMeasurer = rememberTextMeasurer()
    results.forEach { result ->
        Canvas(modifier = Modifier) {
            drawRect(
                color = result.color,
                style = Stroke(width = 5f),
                topLeft = result.topLeft,
                size = result.size
            )
            drawText(
                textMeasurer = textMeasurer,
                topLeft = result.textTopLeft,
                text = result.text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    background = result.color
                ),
                size = result.size
            )
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = System.currentTimeMillis().toString()
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}

@Composable
private fun DrawCaptionResponse(result: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TitleText(
            text = "PaliGemma response:",
        )
        Text(
            text = result,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}
@Composable
private fun TitleText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White
    )
}