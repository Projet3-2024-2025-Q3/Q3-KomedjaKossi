package com.example.jobappbackend.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import org.springframework.security.core.AuthenticationException;

/**
 * Global exception handler for REST controllers.
 * <p>
 * This component converts common exceptions thrown by Spring MVC or the application layer
 * into consistent HTTP responses with a lightweight JSON body ({@link ErrorResponse}).
 * <ul>
 *     <li>400 Bad Request for invalid inputs, type mismatches, or missing multipart parts/parameters</li>
 *     <li>500 Internal Server Error for unexpected failures (e.g., email sending errors)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles domain-level API exceptions raised explicitly by the application layer.
     *
     * @param ex the {@link ApiException} thrown by application services or validators
     * @return HTTP 400 (Bad Request) with a readable error message
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(final ApiException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles invalid path/query parameter types (e.g., "/offers/abc" when a {@code Long} is expected).
     * Triggered by Spring MVC before the controller method is invoked.
     *
     * @param ex the {@link MethodArgumentTypeMismatchException} thrown by the parameter binder
     * @return HTTP 400 (Bad Request) with a short explanatory message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(final MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid parameter type."));
    }

    /**
     * Handles missing multipart file parts (e.g., missing "cv" or "motivation" in a multipart request).
     * Spring throws this before reaching the controller if a required {@code @RequestPart} is absent.
     *
     * @param ex the {@link MissingServletRequestPartException} describing the missing part
     * @return HTTP 400 (Bad Request) with the missing part name
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPart(final MissingServletRequestPartException ex) {
        final String partName = ex.getRequestPartName() != null ? ex.getRequestPartName() : "file";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Missing file part: " + partName));
    }

    /**
     * Handles missing standard (non-multipart) request parameters.
     *
     * @param ex the {@link MissingServletRequestParameterException} describing the missing parameter
     * @return HTTP 400 (Bad Request) with the missing parameter name
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Missing parameter: " + ex.getParameterName()));
    }

    /**
     * Handles invalid arguments explicitly thrown by the application (e.g., failed validations).
     * <p>
     * Note: Use a dedicated exception (e.g., {@code NotFoundException}) mapped to 404 if you need
     * to distinguish "not found" from generic validation errors.
     *
     * @param ex the {@link IllegalArgumentException} thrown by application code
     * @return HTTP 400 (Bad Request) with the original error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(final IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles authentication failures (e.g., bad credentials during login).
     *
     * @param ex the {@link AuthenticationException} thrown by Spring Security
     * @return HTTP 401 (Unauthorized) with a generic error message
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(final AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed: " + ex.getMessage()));
    }


    /**
     * Handles unsupported HTTP methods (e.g., POST used where only PUT is allowed).
     *
     * @param ex the {@link HttpRequestMethodNotSupportedException} thrown by Spring MVC
     * @return HTTP 405 (Method Not Allowed) with a short explanatory message
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("Method not allowed."));
    }

    /**
     * Handles email sending failures coming from the mailing layer.
     *
     * @param ex the {@link MessagingException} thrown during SMTP operations
     * @return HTTP 500 (Internal Server Error) with the original error message
     */
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleMessagingException(final MessagingException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Catch-all handler for any other unhandled exceptions.
     * Keeps internal details out of the response body.
     *
     * @param ex any unexpected {@link Exception}
     * @return HTTP 500 (Internal Server Error) with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(final Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error."));
    }
}
