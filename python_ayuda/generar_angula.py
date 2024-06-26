import math

cities = [
    {
        "name": "Bogota",
        "coords": {"lat": 4.711, "lng": -74.0721},
        "code": "SKBO",
        "GMT": -5,
        "capacidad": 430,
    },
    {
        "name": "Quito",
        "coords": {"lat": -0.1807, "lng": -78.4678},
        "code": "SEQM",
        "GMT": -5,
        "capacidad": 410,
    },
    {
        "name": "Caracas",
        "coords": {"lat": 10.4806, "lng": -66.9036},
        "code": "SVMI",
        "GMT": -4,
        "capacidad": 400,
    }, 
    {
        "name": "Brasilia",
        "coords": { "lat": -15.8267, "lng": -47.9218 },
        "code": "SBBR",
        "GMT": -3,
        "capacidad": 480,
    },
    {
    "name": "Lima",
    "coords": { "lat": -12.0464, "lng": -77.0428 },
    "code": "SPIM",
    "GMT": -5,
    "capacidad": 440,
  },
  {
    "name": "La Paz",
    "coords": { "lat": -16.5, "lng": -68.15 },
    "code": "SLLP",
    "GMT": -4,
    "capacidad": 420,
  },
  {
    "name": "Santiago de Chile",
    "coords": { "lat": -33.4489, "lng": -70.6693 },
    "code": "SCEL",
    "GMT": -3,
    "capacidad": 460,
  },
  {
    "name": "Buenos Aires",
    "coords": { "lat": -34.6037, "lng": -58.3816 },
    "code": "SABE",
    "GMT": -3,
    "capacidad": 460,
  },
  {
    "name": "Asunción",
    "coords": { "lat": -25.2637, "lng": -57.5759 },
    "code": "SGAS",
    "GMT": -4,
    "capacidad": 400,
  },
  {
    "name": "Montevideo",
    "coords": { "lat": -34.9011, "lng": -56.1645 },
    "code": "SUAA",
    "GMT": -3,
    "capacidad": 400,
  },
  {
    "name": "Tirana",
    "coords": { "lat": 41.3275, "lng": 19.8187 },
    "code": "LATI",
    "GMT": 2,
    "capacidad": 410,
  },
  {
    "name": "Berlin",
    "coords": { "lat": 52.52, "lng": 13.405 },
    "code": "EDDI",
    "GMT": 2,
    "capacidad": 480,
  },
  {
    "name": "Viena",
    "coords": { "lat": 48.2082, "lng": 16.3738 },
    "code": "LOWW",
    "GMT": 2,
    "capacidad": 430,
  },
  {
    "name": "Bruselas",
    "coords": { "lat": 50.8503, "lng": 4.3517 },
    "code": "EBCI",
    "GMT": 2,
    "capacidad": 440,
  },
  {
    "name": "Minsk",
    "coords": { "lat": 53.9045, "lng": 27.5615 },
    "code": "UMMS",
    "GMT": 3,
    "capacidad": 400,
  },
  {
    "name": "Sofia",
    "coords": { "lat": 42.6977, "lng": 23.3219 },
    "code": "LBSF",
    "GMT": 3,
    "capacidad": 400,
  },
  {
    "name": "Praga",
    "coords": { "lat": 50.0755, "lng": 14.4378 },
    "code": "LKPR",
    "GMT": 2,
    "capacidad": 400,
  },
  {
    "name": "Zagreb",
    "coords": { "lat": 45.815, "lng": 15.9819 },
    "code": "LDZA",
    "GMT": 2,
    "capacidad": 420,
  },
  {
    "name": "Copenhague",
    "coords": { "lat": 55.6761, "lng": 12.5683 },
    "code": "EKCH",
    "GMT": 2,
    "capacidad": 480,
  },
  {
    "name": "Amsterdam",
    "coords": { "lat": 52.3676, "lng": 4.9041 },
    "code": "EHAM",
    "GMT": 2,
    "capacidad": 480,
  },
  {
    "name": "Delhi",
    "coords": { "lat": 28.7041, "lng": 77.1025 },
    "code": "VIDP",
    "GMT": 5,
    "capacidad": 480,
  },
  {
    "name": "Seul",
    "coords": { "lat": 37.5665, "lng": 126.978 },
    "code": "RKSI",
    "GMT": 9,
    "capacidad": 400,
  },
  {
    "name": "Bangkok",
    "coords": { "lat": 13.7563, "lng": 100.5018 },
    "code": "VTBS",
    "GMT": 7,
    "capacidad": 420,
  },
  {
    "name": "Dubai",
    "coords": { "lat": 25.276987, "lng": 55.296249 },
    "code": "OMDB",
    "GMT": 4,
    "capacidad": 420,
  },
  {
    "name": "Beijing",
    "coords": { "lat": 40.0708, "lng": 116.597 },
    "code": "ZBAA",
    "GMT": 8,
    "capacidad": 480,
  },
  {
    "name": "Tokyo",
    "coords": { "lat": 35.5533, "lng": 139.781 },
    "code": "RJTT",
    "GMT": 9,
    "capacidad": 460,
  },
  {
    "name": "Kuala Lumpur",
    "coords": { "lat": 2.7456, "lng": 101.701 },
    "code": "WMKK",
    "GMT": 8,
    "capacidad": 420,
  },
  {
    "name": "Singapore",
    "coords": { "lat": 1.3571, "lng": 103.987 },
    "code": "WSSS",
    "GMT": 8,
    "capacidad": 400,
  },
  {
    "name": "Jakarta",
    "coords": { "lat": -6.1256, "lng": 106.655 },
    "code": "WIII",
    "GMT": 7,
    "capacidad": 400,
  },
  {
    "name": "Manila",
    "coords": { "lat": 14.5086, "lng": 121.019 },
    "code": "RPLL",
    "GMT": 8,
    "capacidad": 400,
  }
]

cities_by_code = {city["code"]: city for city in cities}

def calculate_rotation_angle(origin, destination):
    return math.atan2(destination["coords"]["lat"] - origin["coords"]["lat"],
                      destination["coords"]["lng"] - origin["coords"]["lng"]) * (180 / math.pi)

routes_angles = []

for origin_code, origin_city in cities_by_code.items():
    for destination_code, destination_city in cities_by_code.items():
        if origin_code != destination_code:
            angle = calculate_rotation_angle(origin_city, destination_city)
            routes_angles.append({
                "origin": origin_code,
                "destination": destination_code,
                "angle": angle
            })

def print_routes_with_angles(routes):
    for route in routes:
        print(f"Ruta: {route['origin']} -> {route['destination']} | Ángulo: {route['angle']:.2f}°")

print_routes_with_angles(routes_angles)
