package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.CofinsBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class CofinsBsDAO {

    public List<CofinsBs> findAll() {
        List<CofinsBs> cofinsBsList = null;
        try {
            Query query = emBs.createQuery("from CofinsBs c");

            cofinsBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cofinsBsList;
    }
}
