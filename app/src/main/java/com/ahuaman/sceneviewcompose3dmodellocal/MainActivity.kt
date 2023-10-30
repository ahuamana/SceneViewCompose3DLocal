package com.ahuaman.sceneviewcompose3dmodellocal

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.ahuaman.sceneviewcompose3dmodellocal.ui.theme.SceneViewCompose3DModelLocalTheme
import com.google.ar.core.Config
import com.google.ar.core.Plane
import io.github.sceneview.Scene
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.model.Model
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberIndirectLight
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberSkybox
import io.github.sceneview.rememberView
import io.github.sceneview.utils.getResourceUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SceneViewCompose3DModelLocalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ThreeDScreen()
                }
            }
        }
    }
}

private const val kModelFile = "https://sceneview.github.io/assets/models/DamagedHelmet.glb"



@Composable
fun ThreeDScreen() {

    val nodes = rememberNodes()
    val eng = rememberEngine()

    val modelLoader:ModelLoader = rememberModelLoader(engine = eng)

    val createModelInstance = modelLoader.createModelInstance(
        "models/animal-cell.glb"
    )

    val modelNode = ModelNode(modelInstance = createModelInstance)
    nodes.add(modelNode)

    Box(modifier = Modifier.fillMaxSize()) {
        Scene(
            activity = LocalContext.current as? ComponentActivity,
            lifecycle = LocalLifecycleOwner.current.lifecycle,
            childNodes = nodes,
            engine = eng,
            modelLoader = modelLoader, //
            materialLoader = rememberMaterialLoader(engine = eng), //Materials function as a templates from which [MaterialInstance]s can be spawned.
            scene = rememberScene(eng), // not needed //Provide your own instance if you want to share [Node]s' scene between multiple views.
            view = rememberView(eng), // It is not advised for an application to use many View objects
            renderer = rememberRenderer(eng),
            cameraNode = rememberCameraNode(engine = eng),
            mainLightNode = rememberMainLightNode(engine = eng),
            indirectLight = rememberIndirectLight(engine = eng),
            skybox = rememberSkybox(eng),
            onViewCreated = ::onViewCreated
        )
    }
}

fun onViewCreated(sceneView: SceneView) {
    println("onViewCreated")
}

fun customResourceResolver(resource: String): String {
    return "file:///models/$resource"
}

/*

@Composable
fun ARSceneView(){
    var isLoading by remember { mutableStateOf(false) }
    var planeRenderer by remember { mutableStateOf(true) }
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val childNodes = rememberNodes()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            modelLoader = modelLoader,
            planeRenderer = planeRenderer,
            onSessionConfiguration = { session, config ->
                config.depthMode =
                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode =
                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onSessionUpdated = { _, frame ->
                if (childNodes.isEmpty()) {
                    frame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { plane ->
                            isLoading = true
                            childNodes += AnchorNode(
                                engine = engine,
                                anchor = plane.createAnchor(plane.centerPose)
                            ).apply {
                                isEditable = true
                                modelLoader.loadModelInstanceAsync(kModelFile) { modelInstance ->
                                    if (modelInstance != null) {
                                        addChildNode(
                                            ModelNode(
                                                modelInstance = modelInstance,
                                                // Scale to fit in a 0.5 meters cube
                                                scaleToUnits = 0.5f,
                                                // Bottom origin instead of center so the model base is on floor
                                                centerOrigin = Position(y = -1.0f)
                                            ).apply {
                                                isEditable = true
                                            }
                                        )
                                    }
                                    isLoading = false
                                    planeRenderer = false
                                }
                            }
                        }
                }
            }
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center),
                color = Color(0xFFBB86FC)
            )
        }
    }
}*/