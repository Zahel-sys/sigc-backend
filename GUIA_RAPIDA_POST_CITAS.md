# 📌 GUÍA RÁPIDA - Endpoint POST /citas

## 🎯 ¿Qué Necesitas Saber?

```
POST /citas
├─ Crea una reserva de cita médica
├─ Requiere token JWT en Authorization header
├─ Valida 8 condiciones de seguridad
└─ Devuelve 201 si es exitoso
```

---

## 🔑 Lo Más Importante

### 1. **Endpoint**
```
POST http://localhost:8080/citas
```

### 2. **Headers OBLIGATORIOS**
```
Authorization: Bearer {token_jwt}
Content-Type: application/json
```

### 3. **Body OBLIGATORIO**
```json
{
  "usuario": { "idUsuario": 1 },
  "horario": { "idHorario": 5 }
}
```

### 4. **Respuesta Exitosa**
```
Código: 201 Created
Retorna: Objeto Cita completo con estado "confirmada"
```

---

## ⚠️ Errores Más Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| 401 | No hay token | Agregar Authorization header |
| 404 | Usuario/horario no existe | Verificar IDs |
| 409 | Horario no disponible | Elegir otro horario |
| 422 | Fecha en el pasado | Elegir horario futuro |

---

## 🧪 Prueba Rápida (Terminal)

```bash
# 1. Obtener token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sigc.com","password":"Admin123456"}' | jq -r '.token')

# 2. Crear cita
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 1}
  }' | jq .
```

---

## 📚 Documentación Completa

- **`ENDPOINT_POST_CITAS.md`** → Referencia API completa
- **`PROMPT_RESERVAR_CITAS_FRONTEND.md`** → Código React + integración
- **`TESTING_POST_CITAS.md`** → 10 casos de prueba con ejemplos
- **`RESUMEN_POST_CITAS.md`** → Resumen técnico detallado

---

## ✅ Estado

```
✅ Implementado     - 100%
✅ Validaciones     - 8/8
✅ Testing          - 10 casos
✅ Documentado      - Completo
✅ Compilado        - SIN ERRORES
✅ Ejecutando       - EN PUERTO 8080
```

**¡LISTO PARA USAR!** 🚀
