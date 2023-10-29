package ru.rsmu.olympreg.entities.system;

/**
 * @author leonid.
 */
public enum StoredPropertyName {
    SENDMAIL_HOST("Sendmail","Адрес сервера отправки почты", "127.0.0.1", StoredPropertyType.STRING),
    SENDMAIL_LOGIN("Sendmail","Логин", "login", StoredPropertyType.STRING),
    SENDMAIL_PASSWORD("Sendmail","Пароль", "password", StoredPropertyType.STRING),
    SENDMAIL_PORT("Sendmail","Номер порта (или 0)", "0", StoredPropertyType.INTEGER),
    SENDMAIL_SSL_PORT("Sendmail","Номер SSL порта", "", StoredPropertyType.STRING),
    SENDMAIL_USE_SSL("Sendmail","Использовать SSL", "0", StoredPropertyType.INTEGER),
    SENDMAIL_USE_TLS("Sendmail","Использовать TLS", "0", StoredPropertyType.INTEGER),

    EMAIL_FROM_ADDRESS("Email","Обратный адрес для email", "olimpiada@rsmu.ru", StoredPropertyType.STRING),
    EMAIL_FROM_SIGNATURE("Email","Название обратного адреса для email", "Организационный комитет Пироговской олимпиады школьников", StoredPropertyType.STRING),

    TEMPOLW_SYSTEM_URL("Integration", "URL системы проведения тестирования", "https://chembio.rsmu.ru", StoredPropertyType.STRING ),
    TEMPOLW_USER("Integration", "Имя пользователя системы проведения тестирования", "prk_admin@rsmu.ru", StoredPropertyType.STRING ),
    TEMPOLW_PASSWORD("Integration", "Пароль системы проведения тестирования", "6A8AEAFFD00E99F5B377B084FA577E3B", StoredPropertyType.STRING ),
    AGREEMENT_FILE_LOCATION("Integration", "Форма согласия на обработку персональных данных", "https://rsmu.ru/fileadmin/templates/DOC/Conferences/Pirogov_olimp_po_himii_i_biologii/forma_sogl_na_obr_PD_o_2020.doc", StoredPropertyType.STRING ),


    VIEW_ONLY_THIS_YEAR_EXAM("Global", "Показывать экзамены только текущего года", "1", StoredPropertyType.INTEGER ),
    MY_OWN_URI("Global", "Адрес данного сервера", "https://olymp.rsmu.ru", StoredPropertyType.STRING ),
    OTHER_COUNTRY_ALLOWED( "Global", "Разрешить участие школьников из других стран", "1", StoredPropertyType.INTEGER ),
    REGISTRATION_AVAILABLE("Global", "Регистрация открыта", "1", StoredPropertyType.INTEGER ),
    REGISTRATION_CHEMISTRY_AVAILABLE("Global", "Регистрация на Химию открыта", "1", StoredPropertyType.INTEGER ),
    REGISTRATION_BIOLOGY_AVAILABLE("Global", "Регистрация на Биологию открыта", "1", StoredPropertyType.INTEGER )
    ;

    private String groupName;
    private String title;
    private String defaultValue;
    private StoredPropertyType type;
    private boolean editable = true;

    StoredPropertyName( String groupName, String title, String defaultValue, StoredPropertyType type ) {
        this.groupName = groupName;
        this.title = title;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    StoredPropertyName( String groupName, String title, String defaultValue, StoredPropertyType type, boolean editable ) {
        this.groupName = groupName;
        this.title = title;
        this.defaultValue = defaultValue;
        this.type = type;
        this.editable = editable;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public StoredPropertyType getType() {
        return type;
    }

    public boolean isEditable() {
        return editable;
    }
}
