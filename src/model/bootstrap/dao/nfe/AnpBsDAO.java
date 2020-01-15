package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.AnpBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class AnpBsDAO {

    public List<AnpBs> findAll() {
        List<AnpBs> anpBsList = null;
        try {
            Query query = emBs.createQuery("from AnpBs c");

            anpBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return anpBsList;
    }
}
