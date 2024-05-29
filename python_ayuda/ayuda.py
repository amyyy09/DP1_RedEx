
import os
# Path to the input files
input_dir = 'python_ayuda/enviado'

# Path to the output file
output_file = 'python_ayuda/combined.txt'

# Lista para almacenar el contenido de todos los archivos
all_data = []


# Iterar sobre cada archivo en el directorio
for filename in os.listdir(input_dir):
    # Construir la ruta completa al archivo
    file_path = os.path.join(input_dir, filename)
    # Asegurar que solo se procesen archivos .txt
    if os.path.isfile(file_path) and file_path.endswith('.txt'):
        # Leer el contenido del archivo
        with open(file_path, 'r') as file:
            data = file.read()
            all_data.append(data)

# Concatenar todo el contenido y escribirlo en el archivo de salida
with open(output_file, 'w') as fo:
    # Si quieres separar el contenido de cada archivo con una nueva línea, puedes descomentar la línea siguiente:
    # fo.write("\n".join(all_data))
    fo.write("".join(all_data))

print('Todos los archivos han sido combinados exitosamente!')