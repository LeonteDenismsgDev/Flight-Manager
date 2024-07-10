package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import msg.flight.manager.persistence.models.company.DBCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyRepository {
    @Autowired
    MongoTemplate template;

    public DBCompany save(DBCompany company){
        return this.template.save(company,"companies");
    }

    public boolean remove(String name){
        Query query = new Query(Criteria.where("_id").is(name));
        DeleteResult result = this.template.remove(query,"companies");
        return result.getDeletedCount()>0;
    }

    public boolean update(String name, DBCompany company){
        if(!this.remove(name)){
            return false;
        }
        if(this.save(company) == null){
            return false;
        }
        return true;
    }

    public DBCompany get(String name){
        Query query = new Query(Criteria.where("_id").is(name));
        return template.findOne(query,DBCompany.class,"companies");
    }

    public List<DBCompany> getAll(){
        return template.findAll(DBCompany.class,"companies");
    }
}
