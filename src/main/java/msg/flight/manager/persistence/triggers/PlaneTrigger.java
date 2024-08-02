package msg.flight.manager.persistence.triggers;

import msg.flight.manager.persistence.models.plane.DBPlane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Configuration
public class PlaneTrigger extends AbstractMongoEventListener<DBPlane> {
    @Autowired
    private MongoTemplate template;

    @Override
    public void onAfterSave(AfterSaveEvent<DBPlane> event) {
        Query query = new Query();
        Update update = new Update().inc("fleet", 1);
        template.upsert(query, update, "companies");
        super.onAfterSave(event);
    }
}

