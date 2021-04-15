package ir.mab.radioamin.model;

public enum ErrorType {
    Exception,
    MethodArgumentNotValidException,
    BindException,
    TypeMismatchException,
    MissingServletRequestPartException,
    MissingServletRequestParameterException,
    MethodArgumentTypeMismatchException,
    ConstraintViolationException,
    HttpRequestMethodNotSupportedException,
    HttpMediaTypeNotSupportedException,
    HttpMediaTypeNotAcceptableException,
    HttpMessageNotReadableException,
    NoHandlerFoundException,
    MissingPathVariableException,
    MissingRequestHeaderException,
    FileStorageException,
    MultipartException,
    ResourceAlreadyExistsException,
    ResourceNotFoundException,
    JpaObjectRetrievalFailureException,
    PropertyReferenceException,
    TokenExpiredException,
    WrongCredentialsException,
    BadCredentialsException,
    DisabledException,
    JWTVerificationException,
    Unauthorized,
    AccessDenied,
}
