package ir.mab.radioamin.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import ir.mab.radioamin.config.ErrorEndpoints;
import ir.mab.radioamin.model.Error;
import ir.mab.radioamin.model.ErrorResponse;
import ir.mab.radioamin.model.ErrorType;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<Error> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {

            Error error1 = new Error(ErrorType.MethodArgumentNotValidException,
                    error.getObjectName()+ "." +error.getField() + ": " + error.getRejectedValue(),
                    error.getField() + ": " + error.getDefaultMessage(),
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.MethodArgumentNotValidException).toUriString()
            );

            errors.add(error1);
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {

            Error error1 = new Error(ErrorType.MethodArgumentNotValidException,
                    error.getObjectName(),
                    error.getObjectName() + ": " + error.getDefaultMessage(),
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.MethodArgumentNotValidException).toUriString()
            );

            errors.add(error1);
        }
        final ErrorResponse errorResponse = new ErrorResponse(errors);
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final List<Error> errors = new ArrayList<>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {

            Error error1 = new Error(ErrorType.BindException,
                    error.getObjectName()+ "." +error.getField(),
                    error.getField() + ": " + error.getDefaultMessage(),
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.BindException).toUriString()
            );

            errors.add(error1);
        }

        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {

            Error error1 = new Error(ErrorType.BindException,
                    error.getObjectName(),
                    error.getObjectName() + ": " + error.getDefaultMessage(),
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.BindException).toUriString()
            );

            errors.add(error1);
        }

        final ErrorResponse errorResponse = new ErrorResponse(errors);

        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        Error error = new Error(ErrorType.TypeMismatchException,
                ex.getPropertyName() + ": " + ex.getValue(),
                ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.TypeMismatchException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.info(ex.getClass().getName());

        String requiredType = "";
        Pattern pattern = Pattern.compile("`(.*?)`");
        Matcher matcher = pattern.matcher(Objects.requireNonNull(ex.getMessage()));

        if (matcher.find())
        {
            requiredType = matcher.group(1);
        }

        String fieldName = ex.getMessage().substring(ex.getMessage().lastIndexOf('[') + 2, ex.getMessage().lastIndexOf(']') - 1);

        Error error = new Error(ErrorType.HttpMessageNotReadableException,
                fieldName + ", RequiredType = " + "( " + requiredType + " )",
                "Syntax error, malformed JSON." ,
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.HttpMessageNotReadableException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);
        return handleExceptionInternal(ex, errorResponse, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        Error error = new Error(ErrorType.MissingServletRequestPartException,
                ex.getRequestPartName(),
                ex.getRequestPartName() + " part is missing",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.MissingServletRequestPartException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        Error error = new Error(ErrorType.MissingServletRequestParameterException,
                ex.getParameterName(),
                ex.getParameterName() + " parameter is missing",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.MissingServletRequestParameterException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error(ErrorType.MissingPathVariableException,
                ex.getVariableName(),
                ex.getParameter()+ " parameter is missing",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.MissingPathVariableException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);
        return handleExceptionInternal(ex, errorResponse, headers, status, request);    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());

        Error error = new Error(ErrorType.MethodArgumentTypeMismatchException,
                ex.getParameter().getParameterName(),
                ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.MethodArgumentTypeMismatchException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final List<Error> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {

            Error error = new Error(ErrorType.ConstraintViolationException,
                    violation.getInvalidValue().toString(),
                    violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage(),
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.ConstraintViolationException).toUriString()
            );
            errors.add(error);

        }

        final ErrorResponse errorResponse = new ErrorResponse(errors);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<Object>  handleTransactionException(TransactionSystemException ex, final WebRequest request) throws Throwable {
        Throwable cause = ex.getCause();
        if (!(cause instanceof RollbackException)) throw cause;
        if (!(cause.getCause() instanceof ConstraintViolationException)) throw cause.getCause();
        ConstraintViolationException validationException = (ConstraintViolationException) cause.getCause();
        final List<Error> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation :  validationException.getConstraintViolations()) {
            Error error = new Error(ErrorType.ConstraintViolationException,
                    violation.getInvalidValue().toString(),
                    violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage(),
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.ConstraintViolationException).toUriString()
            );
            errors.add(error);
        }

        final ErrorResponse errorResponse = new ErrorResponse(errors);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));

        Error error = new Error(ErrorType.HttpRequestMethodNotSupportedException,
                "Method: " + ex.getMethod(),
                builder.toString(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.HttpRequestMethodNotSupportedException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);

    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append("Current media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));

        Error error = new Error(ErrorType.HttpMediaTypeNotAcceptableException,
                "Header MediaType",
                builder.toString(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.HttpMediaTypeNotAcceptableException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));

        Error error = new Error(ErrorType.HttpMediaTypeNotSupportedException,
                Objects.requireNonNull(ex.getContentType()).toString(),
                builder.toString(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.HttpMediaTypeNotSupportedException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error(
                ErrorType.NoHandlerFoundException,
                ex.getRequestURL(),
                ex.getMessage(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.NoHandlerFoundException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }


    @ExceptionHandler(ResourceAlreadyExistsException.class)
    protected ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.ResourceAlreadyExistsException,
                ex.getResource(),
                ex.getResource() + ": " + ex.getValue() + " is already exists.",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.ResourceAlreadyExistsException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.ResourceNotFoundException,
                ex.getResource(),
                ex.getResource() + " with " + ex.getParameter() + " = " + ex.getValue() + " not found.",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.ResourceNotFoundException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.TokenExpiredException,
                ex.getTokenName(),
                ex.getTokenName() + " has already expired",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.TokenExpiredException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(WrongCredentialsException.class)
    protected ResponseEntity<Object> handleWrongCredentialsException(WrongCredentialsException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.WrongCredentialsException,
                ex.getResource() + "." + ex.getParameter(),
                ex.getParameter() + " (value='" + ex.getValue() + "')" + " is wrong.",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.WrongCredentialsException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.BadCredentialsException,
                ex.getMessage(),
                "Invalid email or password.",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.BadCredentialsException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<Object> handleDisabledException(DisabledException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.DisabledException,
                "User",
                ex.getMessage(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.DisabledException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(JWTVerificationException.class)
    protected ResponseEntity<Object> handleJWTVerificationException(JWTVerificationException ex, WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.JWTVerificationException,
                "accessToken",
                ex.getMessage(),
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.JWTVerificationException).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAll(Exception ex,WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        Error error = new Error(ErrorType.Exception,
                ex.getCause() == null ? ex.getMessage():ex.getCause().toString(),
                "An Exception Occurred, Call Developers!",
                ServletUriComponentsBuilder.fromCurrentContextPath().path(ErrorEndpoints.InternalServerError).toUriString()
        );
        final ErrorResponse errorResponse = new ErrorResponse(error);

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
