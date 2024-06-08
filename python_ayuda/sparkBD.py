from pyspark.sql import SparkSession
from pyspark.sql.functions import split, col, concat_ws, monotonically_increasing_id, current_timestamp, lit, to_timestamp, date_format
from pyspark.sql.types import IntegerType

# Configurar SparkSession
spark = SparkSession.builder \
    .appName("LargeFileToMySQL") \
    .config("spark.sql.shuffle.partitions", "2048") \
    .getOrCreate()


# Leer el archivo CSV en un DataFrame de Spark
file_path = "python_ayuda/enviado/pack_enviado_EHAM.txt"
df = spark.read.csv(file_path)

# Mostrar un ejemplo del DataFrame
df.show(5, truncate=False)

# Dividir la columna en varias columnas usando expresiones regulares
split_col = split(df['_c0'], '-')
df = df.withColumn('CiudadOrigen', split_col.getItem(0)) \
    .withColumn('IdentificadorEnvio', split_col.getItem(1)) \
    .withColumn('FechaEnvio', split_col.getItem(2)) \
    .withColumn('HoraEnvio', split_col.getItem(3)) \
    .withColumn('CiudadDestinoCantidad', split_col.getItem(4))

# Separar CiudadDestino y Cantidad de paquetes
split_col_dest_cant = split(df['CiudadDestinoCantidad'], ':')
df = df.withColumn('CiudadDestino', split_col_dest_cant.getItem(0)) \
    .withColumn('CantidadPaquetes', split_col_dest_cant.getItem(1))
# Obtener el tamaño del DataFrame filtrado
df_size = df.count()
print(f"El tamaño del DataFrame antes del filtrado es: {df_size} filas")


# Crear la columna de fecha y hora combinada
df = df.withColumn('fechaHoraOrigen', concat_ws(' ', df['FechaEnvio'], df['HoraEnvio']))
# Verificar la columna concatenada
df.select('fechaHoraOrigen').show(10, truncate=False)
# Convertir la columna de fecha y hora a timestamp incluyendo milisegundos
df = df.withColumn('fechaHoraOrigen', to_timestamp(col('fechaHoraOrigen'), 'yyyyMMdd HH:mm:ss.00000'))



# Obtener la fecha actual en formato 'yyyyMMdd'
current_date_str = date_format(current_timestamp(), 'yyyyMMdd')
# Filtrar los envíos con fechaHoraOrigen mayor o igual al día de hoy
df = df.filter(col('FechaEnvio') >= current_date_str)
# Obtener el tamaño del DataFrame filtrado
df_size = df.count()
print(f"El tamaño del DataFrame filtrado es: {df_size} filas")
df.show(10, truncate=False)


# Asignar valores adicionales para los nuevos campos
df = df.withColumn("activo", lit(1))  # Asignar valor 1 a activo
df = df.withColumn("fecha_creacion", current_timestamp())
df = df.withColumn("fecha_modificacion", current_timestamp())
df = df.withColumn("zona_horariagmt", lit(0))  # Asignar valor 0 a zona_horariagmt
df = df.withColumn("id_aeropuerto_destino", lit(None).cast(IntegerType()))  # Asignar NULL a id_aeropuerto_destino
df = df.withColumn("id_aeropuerto_origen", lit(None).cast(IntegerType()))  # Asignar NULL a id_aeropuerto_origen

# Seleccionar y renombrar las columnas finales para la tabla ENVIOS
envios_df = df.select(
    col('activo').alias('activo'),
    col('fecha_creacion').alias('fecha_creacion'),
    col('fecha_modificacion').alias('fecha_modificacion'),
    col('CantidadPaquetes').alias('cant_paquetes'),
    col('CiudadDestino').alias('codigoiatadestino'),
    col('CiudadOrigen').alias('codigoiataorigen'),
    col('fechaHoraOrigen').alias('fecha_hora_origen'),
    col('IdentificadorEnvio').alias('id_envio'),
    col('zona_horariagmt').alias('zona_horariagmt')
)


# Añadir una columna ID única para cada fila utilizando monotonically_increasing_id y UUID
# from pyspark.sql.functions import expr

# envios_df = envios_df.withColumn("id", expr("uuid()"))

# Añadir una columna ID única para cada fila utilizando monotonically_increasing_id y transformándola en int

envios_df.show(10, truncate=False)

# Reparticionar el DataFrame
num_partitions = 2048   # Puedes ajustar este número según los recursos disponibles
envios_df = envios_df.repartition(num_partitions)


# Configurar la conexión a MySQL
jdbc_url = "jdbc:mysql://db-redex-1a.c01nmehbjhju.us-east-1.rds.amazonaws.com:3306/Danny"
connection_properties = {
    "user": "admin",
    "password": "adaviladp1",
    "driver": "com.mysql.cj.jdbc.Driver",
    "batchsize": "1000",  # Tamaño del lote
    "rewriteBatchedStatements": "true",  # Permite reescribir declaraciones en lotes
    "autocommit": "false"  # Desactivar autocommit
}

# Escribir el DataFrame en la base de datos MySQL
try:
    # Guardar los datos en la tabla ENVIOS usando múltiples hilos
    envios_df.write \
        .jdbc(url=jdbc_url, table="envio", mode="append", properties=connection_properties)
    
    # # Guardar los datos en la tabla ENVIOS
    # envios_df.write.jdbc(url=jdbc_url, table="envio", mode="append", properties=connection_properties)
    
    print("Datos cargados exitosamente en la base de datos MySQL")
except Exception as e:
    print(f"Error al cargar datos en la base de datos MySQL: {e}")

# Detener SparkSession
spark.stop()
