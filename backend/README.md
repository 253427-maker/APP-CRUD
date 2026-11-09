# Backend — Control de Cultivos

Backend de prueba usando **json-server 0.17.4** + **json-server-auth 2.1.0**.

## Requisitos
- Node.js 18+ instalado  
- PowerShell (Windows)

## Instalación (una sola vez)

```powershell
cd AppCRUD\backend
npm install
```

## Ejecutar el servidor

```powershell
cd AppCRUD\backend
npm start
```

El servidor queda disponible en **http://localhost:3000**

## Endpoints disponibles

| Método | URL | Protección | Descripción |
|--------|-----|-----------|-------------|
| POST | /register | ❌ Pública | Registrar usuario nuevo |
| POST | /login | ❌ Pública | Login → devuelve `accessToken` |
| GET | /parcelas | ✅ Bearer token | Listar todas las parcelas |
| POST | /parcelas | ✅ Bearer token | Crear parcela |
| GET | /parcelas/{id} | ✅ Bearer token | Ver una parcela |
| PUT | /parcelas/{id} | ✅ Bearer token | Editar parcela |
| DELETE | /parcelas/{id} | ✅ Bearer token | Eliminar parcela |

## Ejemplo de registro y login

```powershell
# Registrar usuario
Invoke-RestMethod -Method Post -Uri "http://localhost:3000/register" `
  -ContentType "application/json" `
  -Body '{"name":"Karen","email":"karen@test.com","password":"12345678"}'

# Login
Invoke-RestMethod -Method Post -Uri "http://localhost:3000/login" `
  -ContentType "application/json" `
  -Body '{"email":"karen@test.com","password":"12345678"}'
# → Responde con { accessToken: "eyJ...", user: {...} }
```

## Desde el emulador Android

La app usa `http://10.0.2.2:3000/` — esa IP es el alias que el emulador usa
para acceder a `localhost` del PC. Con el servidor corriendo en el PC,
el emulador se conecta automáticamente.

## Notas
- El archivo `db.json` es la "base de datos" — json-server lo modifica en disco.
- Si quieres volver al estado inicial, restaura el `db.json` original desde Git.
- La regla `"parcelas": 660` en `routes.json` significa que las rutas `/parcelas`
  requieren token válido (lectura y escritura). El valor 660 es un código de
  permiso de json-server-auth.
