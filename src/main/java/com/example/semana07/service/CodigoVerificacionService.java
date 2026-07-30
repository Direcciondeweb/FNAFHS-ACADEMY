package com.example.semana07.service;

import com.example.semana07.entity.CodigoVerificacion;
import com.example.semana07.entity.Usuario;
import com.example.semana07.repository.CodigoVerificacionRepository;
import com.example.semana07.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class CodigoVerificacionService {

    private static final int MINUTOS_EXPIRACION = 15;
    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 5;

    @Autowired private CodigoVerificacionRepository codigoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    /** Genera y envía un nuevo código. Se puede llamar tantas veces como se quiera (reenviar). */
    public void solicitarCodigo(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            // No revelamos si el email existe o no, por seguridad.
            return;
        }

        String codigo = String.format("%06d", random.nextInt(1_000_000));

        CodigoVerificacion cv = new CodigoVerificacion();
        cv.setEmail(email);
        cv.setCodigo(codigo);
        cv.setExpiracion(LocalDateTime.now().plusMinutes(MINUTOS_EXPIRACION));
        cv.setIntentosFallidos(0);
        cv.setUsado(false);
        codigoRepository.save(cv);

        emailService.sendVerificationCode(email, codigo);
    }

    /** Estado actual: si está bloqueado y cuántos segundos faltan. */
    public EstadoBloqueo consultarBloqueo(String email) {
        Optional<CodigoVerificacion> ultimo = codigoRepository.findTopByEmailOrderByFechaCreacionDesc(email);
        if (ultimo.isEmpty()) return new EstadoBloqueo(false, 0);

        CodigoVerificacion cv = ultimo.get();
        if (cv.getBloqueadoHasta() != null && cv.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            long segundos = ChronoUnit.SECONDS.between(LocalDateTime.now(), cv.getBloqueadoHasta());
            return new EstadoBloqueo(true, Math.max(segundos, 0));
        }
        return new EstadoBloqueo(false, 0);
    }

    /**
     * Verifica el código. Devuelve true si es válido.
     * Si falla, incrementa el contador; al llegar a 3 fallos bloquea 5 minutos.
     */
    public ResultadoVerificacion verificarCodigo(String email, String codigoIngresado) {
        Optional<CodigoVerificacion> ultimo = codigoRepository.findTopByEmailOrderByFechaCreacionDesc(email);
        if (ultimo.isEmpty()) {
            return ResultadoVerificacion.error("No se encontró un código para este correo. Solicita uno nuevo.");
        }

        CodigoVerificacion cv = ultimo.get();

        if (cv.getBloqueadoHasta() != null && cv.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            long segundos = ChronoUnit.SECONDS.between(LocalDateTime.now(), cv.getBloqueadoHasta());
            return ResultadoVerificacion.bloqueado(segundos);
        }

        if (cv.isUsado()) {
            return ResultadoVerificacion.error("Este código ya fue utilizado. Solicita uno nuevo.");
        }

        if (cv.getExpiracion().isBefore(LocalDateTime.now())) {
            return ResultadoVerificacion.error("El código expiró. Solicita uno nuevo.");
        }

        if (!cv.getCodigo().equals(codigoIngresado.trim())) {
            cv.setIntentosFallidos(cv.getIntentosFallidos() + 1);

            if (cv.getIntentosFallidos() >= MAX_INTENTOS) {
                cv.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
                codigoRepository.save(cv);
                return ResultadoVerificacion.bloqueado(MINUTOS_BLOQUEO * 60L);
            }

            codigoRepository.save(cv);
            int restantes = MAX_INTENTOS - cv.getIntentosFallidos();
            return ResultadoVerificacion.error("Código incorrecto. Te quedan " + restantes + " intento(s).");
        }

        return ResultadoVerificacion.exito();
    }

    /** Cambia la contraseña si el código es válido, y marca el código como usado. */
    public ResultadoVerificacion cambiarPasswordConCodigo(String email, String codigo, String nuevaPassword) {
        ResultadoVerificacion verificacion = verificarCodigo(email, codigo);
        if (!verificacion.isExito()) {
            return verificacion;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResultadoVerificacion.error("Usuario no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        CodigoVerificacion cv = codigoRepository.findTopByEmailOrderByFechaCreacionDesc(email).get();
        cv.setUsado(true);
        codigoRepository.save(cv);

        emailService.sendPasswordChangedConfirmation(email, usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : usuario.getUsername());

        return ResultadoVerificacion.exito();
    }

    public record EstadoBloqueo(boolean bloqueado, long segundosRestantes) {}

    public static class ResultadoVerificacion {
        private final boolean exito;
        private final boolean bloqueado;
        private final long segundosBloqueo;
        private final String mensaje;

        private ResultadoVerificacion(boolean exito, boolean bloqueado, long segundosBloqueo, String mensaje) {
            this.exito = exito;
            this.bloqueado = bloqueado;
            this.segundosBloqueo = segundosBloqueo;
            this.mensaje = mensaje;
        }

        public static ResultadoVerificacion exito() {
            return new ResultadoVerificacion(true, false, 0, "Código verificado correctamente");
        }

        public static ResultadoVerificacion error(String mensaje) {
            return new ResultadoVerificacion(false, false, 0, mensaje);
        }

        public static ResultadoVerificacion bloqueado(long segundos) {
            return new ResultadoVerificacion(false, true, segundos,
                    "Demasiados intentos fallidos. Intenta de nuevo en " + (segundos / 60) + " minutos.");
        }

        public boolean isExito() { return exito; }
        public boolean isBloqueado() { return bloqueado; }
        public long getSegundosBloqueo() { return segundosBloqueo; }
        public String getMensaje() { return mensaje; }
    }
}