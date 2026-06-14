# 📚 API Documentation - FNAFHS Academy

## Base URL

| Entorno | URL |
|---------|-----|
| **Local** | `http://localhost:8080` |
| **Producción** | `https://fnafhs-academy-backend.onrender.com` |

## Autenticación

Actualmente la API no requiere token de autenticación para endpoints de lectura.  
Los endpoints de escritura requieren sesión activa (por implementar JWT).

---

## 🧑‍🤝‍🧑 Usuarios

### GET `/api/usuarios`
Listar todos los usuarios registrados.

**Response:**
```json
[
  {
    "id": 1,
    "username": "admin",
    "nombreCompleto": "Administrador",
    "email": "admin@gmail.com",
    "rol": "ADMIN",
    "estado": 1
  }
]