import pandas as pd
import numpy as np
import re
# Open the file
with open("C:\Users\danny\Documents\PERSONAL\resultados.txt", 'r',  encoding='utf-16') as file:
    content = file.read()

# Define the pattern
pattern = r"Empezando a ejecutar genetico.. en el tiempo de ejecuci¾n: (\d+)(.?No atendidos: (\d+).?Resultado del pso procesado en el tiempo de ejecuci¾n: (\d+))"

# Find all sequences
sequences = re.findall(pattern, content, re.DOTALL)

# Prepare a list to store the data
data = []

# Calculate and store the differences, averages, and "No atendidos:" values
for i, (start, middle, not_attended, end) in enumerate(sequences, 1):
    difference = (int(end) - int(start))/1000

    # Find all "Minutos que lleg¾ antes:" values
    minutes_pattern = r"Minutos que lleg¾ antes: (\d+)"
    minutes_values = re.findall(minutes_pattern, middle)

    # Calculate the average
    average = None
    if minutes_values:
        average = sum(int(value) for value in minutes_values) / len(minutes_values)

    # Append the data
    data.append([difference, average, not_attended])

# Create a DataFrame
df = pd.DataFrame(data, columns=['tejecucion', 'prom_tiempo_restante', 'npedidosNoAtendidos'])
df.index.name = 'niteracion'
df.to_csv('D:\\Pucp\\2024-1\\DP1\\output.csv', sep=';')