import tensorflow as tf

# Convert the model
converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir) # path to the SavedModel directory
#converter = tf.lite.TFLiteConverter.from_saved_model('exported_model/saved_model')
tflite_model = converter.convert()

# Save the model.
with open('model.tflite', 'wb') as f:
#with open('exported_model/saved_model/model.tflite', 'wb') as f:
  f.write(tflite_model)
f.close()