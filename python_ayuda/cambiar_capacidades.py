import json

# Ruta del archivo .ts
file_path = 'python_ayuda/flightPlans.ts'

# Leer el contenido del archivo .ts
with open(file_path, 'r') as file:
    flight_plans_ts = file.read()

# Extraer la parte JSON de la cadena (eliminar 'export const flightPlans = ' y el punto y coma al final)
json_str = flight_plans_ts.replace('export const flightPlans = ', '').strip().rstrip(';')


flight_plans = json.loads(json_str)


for plan in flight_plans:
    plan['capacidad'] = 180
    plan['origin']['capacidad'] = 1350
    plan['destiny']['capacidad'] = 1350


updated_json_str = json.dumps(flight_plans, indent=2)

# Formatear como TypeScript
updated_ts_str = f"export const flightPlans = {updated_json_str};"

# Guardar el resultado en el archivo .ts
with open(file_path, 'w') as file:
    file.write(updated_ts_str)

print("El archivo ha sido actualizado con éxito.")
