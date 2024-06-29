import math

cities = [
    {"name": "Bogota", "coords": {"lat": 4.711, "lng": -74.0721}, "code": "SKBO", "GMT": -5, "capacidad": 430},
    {"name": "Quito", "coords": {"lat": -0.1807, "lng": -78.4678}, "code": "SEQM", "GMT": -5, "capacidad": 410},
    {"name": "Caracas", "coords": {"lat": 10.4806, "lng": -66.9036}, "code": "SVMI", "GMT": -4, "capacidad": 400},
    {"name": "Brasilia", "coords": {"lat": -15.8267, "lng": -47.9218}, "code": "SBBR", "GMT": -3, "capacidad": 480},
    {"name": "Lima", "coords": {"lat": -12.0464, "lng": -77.0428}, "code": "SPIM", "GMT": -5, "capacidad": 440},
    {"name": "La Paz", "coords": {"lat": -16.5, "lng": -68.15}, "code": "SLLP", "GMT": -4, "capacidad": 420},
    {"name": "Santiago de Chile", "coords": {"lat": -33.4489, "lng": -70.6693}, "code": "SCEL", "GMT": -3, "capacidad": 460},
    {"name": "Buenos Aires", "coords": {"lat": -34.6037, "lng": -58.3816}, "code": "SABE", "GMT": -3, "capacidad": 460},
    {"name": "Asunción", "coords": {"lat": -25.2637, "lng": -57.5759}, "code": "SGAS", "GMT": -4, "capacidad": 400},
    {"name": "Montevideo", "coords": {"lat": -34.9011, "lng": -56.1645}, "code": "SUAA", "GMT": -3, "capacidad": 400},
    {"name": "Tirana", "coords": {"lat": 41.3275, "lng": 19.8187}, "code": "LATI", "GMT": 2, "capacidad": 410},
    {"name": "Berlin", "coords": {"lat": 52.52, "lng": 13.405}, "code": "EDDI", "GMT": 2, "capacidad": 480},
    {"name": "Viena", "coords": {"lat": 48.2082, "lng": 16.3738}, "code": "LOWW", "GMT": 2, "capacidad": 430},
    {"name": "Bruselas", "coords": {"lat": 50.8503, "lng": 4.3517}, "code": "EBCI", "GMT": 2, "capacidad": 440},
    {"name": "Minsk", "coords": {"lat": 53.9045, "lng": 27.5615}, "code": "UMMS", "GMT": 3, "capacidad": 400},
    {"name": "Sofia", "coords": {"lat": 42.6977, "lng": 23.3219}, "code": "LBSF", "GMT": 3, "capacidad": 400},
    {"name": "Praga", "coords": {"lat": 50.0755, "lng": 14.4378}, "code": "LKPR", "GMT": 2, "capacidad": 400},
    {"name": "Zagreb", "coords": {"lat": 45.815, "lng": 15.9819}, "code": "LDZA", "GMT": 2, "capacidad": 420},
    {"name": "Copenhague", "coords": {"lat": 55.6761, "lng": 12.5683}, "code": "EKCH", "GMT": 2, "capacidad": 480},
    {"name": "Amsterdam", "coords": {"lat": 52.3676, "lng": 4.9041}, "code": "EHAM", "GMT": 2, "capacidad": 480},
    {"name": "Delhi", "coords": {"lat": 28.7041, "lng": 77.1025}, "code": "VIDP", "GMT": 5, "capacidad": 480},
    {"name": "Seul", "coords": {"lat": 37.5665, "lng": 126.978}, "code": "RKSI", "GMT": 9, "capacidad": 400},
    {"name": "Bangkok", "coords": {"lat": 13.7563, "lng": 100.5018}, "code": "VTBS", "GMT": 7, "capacidad": 420},
    {"name": "Dubai", "coords": {"lat": 25.276987, "lng": 55.296249}, "code": "OMDB", "GMT": 4, "capacidad": 420},
    {"name": "Beijing", "coords": {"lat": 40.0708, "lng": 116.597}, "code": "ZBAA", "GMT": 8, "capacidad": 480},
    {"name": "Tokyo", "coords": {"lat": 35.5533, "lng": 139.781}, "code": "RJTT", "GMT": 9, "capacidad": 460},
    {"name": "Kuala Lumpur", "coords": {"lat": 2.7456, "lng": 101.701}, "code": "WMKK", "GMT": 8, "capacidad": 420},
    {"name": "Singapore", "coords": {"lat": 1.3571, "lng": 103.987}, "code": "WSSS", "GMT": 8, "capacidad": 400},
    {"name": "Jakarta", "coords": {"lat": -6.1256, "lng": 106.655}, "code": "WIII", "GMT": 7, "capacidad": 400},
    {"name": "Manila", "coords": {"lat": 14.5086, "lng": 121.019}, "code": "RPLL", "GMT": 8, "capacidad": 400},
    {"name": "Damasco", "coords": {"lat": 33.4114, "lng": 36.5156}, "code": "OSDI", "GMT": 3, "capacidad": 400},
    {"name": "Riad", "coords": {"lat": 24.9511, "lng": 46.6989}, "code": "OERK", "GMT": 3, "capacidad": 420},
    {"name": "Kabul", "coords": {"lat": 34.565, "lng": 69.211}, "code": "OAKB", "GMT": 4, "capacidad": 480},
    {"name": "Mascate", "coords": {"lat": 23.5868, "lng": 58.4061}, "code": "OOMS", "GMT": 4, "capacidad": 460},
    {"name": "Sana", "coords": {"lat": 15.4761, "lng": 44.2197}, "code": "OYSN", "GMT": 3, "capacidad": 420},
    {"name": "Karachi", "coords": {"lat": 24.8607, "lng": 67.0011}, "code": "OPKC", "GMT": 5, "capacidad": 410},
    {"name": "Baku", "coords": {"lat": 40.4444, "lng": 50.0044}, "code": "UBBB", "GMT": 2, "capacidad": 400},
    {"name": "Aman", "coords": {"lat": 31.7392, "lng": 35.9887}, "code": "OJAI", "GMT": 3, "capacidad": 400},
]

cities_by_code = {city["code"]: city for city in cities}

def calculate_bearing(begin, end):
    lat = abs(begin["coords"]["lat"] - end["coords"]["lat"])
    lng = abs(begin["coords"]["lng"] - end["coords"]["lng"])

    if begin["coords"]["lat"] < end["coords"]["lat"] and begin["coords"]["lng"] < end["coords"]["lng"]:
        return math.atan(lng / lat) * 180 / math.pi
    elif begin["coords"]["lat"] >= end["coords"]["lat"] and begin["coords"]["lng"] < end["coords"]["lng"]:
        return (90 - math.atan(lng / lat) * 180 / math.pi) + 90
    elif begin["coords"]["lat"] >= end["coords"]["lat"] and begin["coords"]["lng"] >= end["coords"]["lng"]:
        return math.atan(lng / lat) * 180 / math.pi + 180
    elif begin["coords"]["lat"] < end["coords"]["lat"] and begin["coords"]["lng"] >= end["coords"]["lng"]:
        return (90 - math.atan(lng / lat) * 180 / math.pi) + 270
    return -1

routes_angles = []

for origin_code, origin_city in cities_by_code.items():
    for destination_code, destination_city in cities_by_code.items():
        if origin_code != destination_code:
            angle = calculate_bearing(origin_city, destination_city)
            routes_angles.append({
                "origin": origin_code,
                "destination": destination_code,
                "angle": angle
            })

def read_flight_plans(file_path):
    flight_plans = []
    with open(file_path, 'r') as file:
        for line in file:
            parts = line.strip().split('-')
            origin_code, dest_code, dep_time, arr_time, capacidad = parts
            origin_city = cities_by_code[origin_code]
            dest_city = cities_by_code[dest_code]
            flight_plan = {
                "origin": origin_city,
                "destiny": dest_city,
                "departureTime": dep_time,
                "arrivalTime": arr_time,
                "capacidad": int(capacidad),
            }
            flight_plans.append(flight_plan)
    return flight_plans

def generate_ts_text(flight_plans, routes_angles):
    ts_text = "export const flightPlans = [\n"
    for idx, plan in enumerate(flight_plans, 1):
        ts_text += f"  {{\n"
        ts_text += f"    origin: {{\n"
        ts_text += f"      name: \"{plan['origin']['name']}\",\n"
        ts_text += f"      coords: {{ lat: {plan['origin']['coords']['lat']}, lng: {plan['origin']['coords']['lng']} }},\n"
        ts_text += f"      code: \"{plan['origin']['code']}\",\n"
        ts_text += f"      GMT: {plan['origin']['GMT']},\n"
        ts_text += f"      capacidad: {plan['origin']['capacidad']},\n"
        ts_text += f"    }},\n"
        ts_text += f"    destiny: {{\n"
        ts_text += f"      name: \"{plan['destiny']['name']}\",\n"
        ts_text += f"      coords: {{ lat: {plan['destiny']['coords']['lat']}, lng: {plan['destiny']['coords']['lng']} }},\n"
        ts_text += f"      code: \"{plan['destiny']['code']}\",\n"
        ts_text += f"      GMT: {plan['destiny']['GMT']},\n"
        ts_text += f"      capacidad: {plan['destiny']['capacidad']},\n"
        ts_text += f"    }},\n"
        ts_text += f"    departureTime: \"{plan['departureTime']}\",\n"
        ts_text += f"    arrivalTime: \"{plan['arrivalTime']}\",\n"
        ts_text += f"    capacidad: {plan['capacidad']},\n"
        ts_text += f"    code: {idx},\n"
        ts_text += f"  }},\n"
    ts_text += "];\n\n"
    ts_text += "interface Route {\n"
    ts_text += "  origin: string;\n"
    ts_text += "  destination: string;\n"
    ts_text += "  angle: number;\n"
    ts_text += "}\n\n"
    ts_text += "export const routesAngles: Route[] = [\n"
    for route in routes_angles:
        ts_text += f"  {{ origin: '{route['origin']}', destination: '{route['destination']}', angle: {route['angle']:.2f} }},\n"
    ts_text += "];"
    return ts_text

# Ruta del archivo de texto
file_path = 'flight_plans.txt'

# Leer los planes de vuelo desde el archivo
flight_plans = read_flight_plans(file_path)

# Generar el texto en formato TypeScript
ts_text = generate_ts_text(flight_plans, routes_angles)

# Imprimir el texto
print(ts_text)
