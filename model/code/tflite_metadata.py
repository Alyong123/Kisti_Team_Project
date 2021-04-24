from tflite_support import metadata as _metadata
from tflite_support import metadata_schema_py_generated as _metadata_fb
import flatbuffers
import os

model_meta = _metadata_fb.ModelMetadataT()
model_meta.name = "MobilenNetV2 SSD 300x300 Object Detector"
model_meta.description = ("Localize the most prominent object in the"
                          "image from a set of N categories such as"
                          "Crosswalk, Green/Red signal, Car")
model_meta.version = 'V1'
model_meta.author = "Phoenix"
model_meta.license = ('Apache License. Version 2.0 '
                      'http://www.apache.org/licenses/LICENSE-2.0. ')

input_meta  = _metadata_fb.TensorMetadataT()

input_meta.name = 'Image'
input_meta.description = (
    "Input image to be classified. The expected image is {0} x {1}, with "
    "three channels (red, blue, green) per pixel. Each value in the "
    "tensor is a single byte between 0 and 255. ".format(640, 640))
input_meta.content = _metadata_fb.ContentT()
input_meta.content.contentProperties = _metadata_fb.ImagePropertiesT()
input_meta.content.contentProperties.colorSpace = _metadata_fb.ColorSpaceType.RGB
input_meta.content.contentPropertiesType = _metadata_fb.ContentProperties.ImageProperties
input_normalization = _metadata_fb.ProcessUnitT()
input_normalization.optionsType = _metadata_fb.ProcessUnitOptions.NormalizationOptions
input_normalization.options = _metadata_fb.NormalizationOptionsT()
input_normalization.options.mean = [127.5]
input_normalization.options.std = [127.5]
input_meta.processUnits = [input_normalization]
input_stats = _metadata_fb.StatsT()
input_stats.max = [255]
input_stats.min = [0]
input_meta.stats = input_stats

labelmap_file = "F:/Android/object_detection/android/app/src/main/assets/labelmap.txt"
exported_model_path = "F:/Android/object_detection/android/app/src/main/assets/model_fpnlite640.tflite"

output_tensorgroups = _metadata_fb.TensorGroupT()
output_tensorgroups.name = "detection result"
output_tensorgroups.tensorNames = ["location", "category", "score"]

location = _metadata_fb.TensorMetadataT()
location.name = "location"
location.description = "The location of the detected boxes. "
location.content = _metadata_fb.ContentT()
location.content.contentProperties = (_metadata_fb.BoundingBoxPropertiesT())
location.content.contentProperties.index = [1,0,3,2]
location.content.contentProperties.type = (_metadata_fb.BoundingBoxType.BOUNDARIES)
location.content.contentProperties.coordinateType = (_metadata_fb.CoordinateType.RATIO)
location.content.contentPropertiesType = (_metadata_fb.ContentProperties.BoundingBoxProperties)
location.content.range = _metadata_fb.ValueRangeT()
location.content.range.min = 2
location.content.range.max = 2

category = _metadata_fb.TensorMetadataT()
category.name = "category"
category.description = "The categories of the detected boxes. "
category.content = _metadata_fb.ContentT()
category.content.contentPropertiesType = (_metadata_fb.ContentProperties.FeatureProperties)
category.content.contentProperties = (_metadata_fb.FeaturePropertiesT())
category.content.range = _metadata_fb.ValueRangeT()
category.content.range.min = 2
category.content.range.max = 2

label_file = _metadata_fb.AssociatedFileT()
label_file.name = os.path.basename(labelmap_file)
label_file.description = 'Label of objects that this model can recognize. '
label_file.type = _metadata_fb.AssociatedFileType.TENSOR_VALUE_LABELS
category.associatedFiles = [label_file]

score = _metadata_fb.TensorMetadataT()
score.name = "score"
score.description = "The score of the detected boxes. "
score.content = _metadata_fb.ContentT()
score.content.contentPropertiesType = (_metadata_fb.ContentProperties.FeatureProperties)
score.content.contentProperties = (_metadata_fb.FeaturePropertiesT())
score.content.range = _metadata_fb.ValueRangeT()
score.content.range.max = 2
score.content.range.min = 2

num_of_detection = _metadata_fb.TensorMetadataT()
num_of_detection.name = "number of detections"
num_of_detection.description = "The number of the detected boxes. "
num_of_detection.content = _metadata_fb.ContentT()
num_of_detection.content.contentPropertiesType = (_metadata_fb.ContentProperties.FeatureProperties)
num_of_detection.content.contentProperties = (_metadata_fb.FeaturePropertiesT())



subgraph = _metadata_fb.SubGraphMetadataT()
subgraph.outputTensorGroups = [output_tensorgroups]
subgraph.inputTensorMetadata = [input_meta]
subgraph.outputTensorMetadata = [location, category, score, num_of_detection]
model_meta.subgraphMetadata = [subgraph]

b = flatbuffers.Builder(0)
b.Finish(
    model_meta.Pack(b),
    _metadata.MetadataPopulator.METADATA_FILE_IDENTIFIER)
metadata_buf = b.Output()

populator = _metadata.MetadataPopulator.with_model_file(exported_model_path)
populator.load_metadata_buffer(metadata_buf)
populator.load_associated_files([labelmap_file])
populator.populate()

displayer = _metadata.MetadataDisplayer.with_model_file(exported_model_path)
export_json_file = os.path.join('C:/Users/anywh/Desktop/cetification/android/object_detection/android/app/src/main/assets', "colab_v2.json")
json_file = displayer.get_metadata_json()