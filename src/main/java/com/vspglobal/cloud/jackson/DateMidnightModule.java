package com.vspglobal.cloud.jackson;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateMidnight;

public class DateMidnightModule extends SimpleModule {
    private static final long serialVersionUID = 7196929338888298023L;

    public DateMidnightModule() {
        super(DateMidnightModule.class.getSimpleName(), new Version(1,0,0,null));
        addDeserializer(DateMidnight.class, new DateMidnightDeserializer());
    }

}

