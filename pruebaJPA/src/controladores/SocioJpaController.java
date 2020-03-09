/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Barco;
import entidades.Socio;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author labcisco
 */
public class SocioJpaController implements Serializable {

    public SocioJpaController() {
        this.emf = Persistence.createEntityManagerFactory("pruebaJPAPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Socio socio) throws PreexistingEntityException, Exception {
        if (socio.getBarcoList() == null) {
            socio.setBarcoList(new ArrayList<Barco>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Barco> attachedBarcoList = new ArrayList<Barco>();
            for (Barco barcoListBarcoToAttach : socio.getBarcoList()) {
                barcoListBarcoToAttach = em.getReference(barcoListBarcoToAttach.getClass(), barcoListBarcoToAttach.getIdBarco());
                attachedBarcoList.add(barcoListBarcoToAttach);
            }
            socio.setBarcoList(attachedBarcoList);
            em.persist(socio);
            for (Barco barcoListBarco : socio.getBarcoList()) {
                Socio oldIdSocioOfBarcoListBarco = barcoListBarco.getIdSocio();
                barcoListBarco.setIdSocio(socio);
                barcoListBarco = em.merge(barcoListBarco);
                if (oldIdSocioOfBarcoListBarco != null) {
                    oldIdSocioOfBarcoListBarco.getBarcoList().remove(barcoListBarco);
                    oldIdSocioOfBarcoListBarco = em.merge(oldIdSocioOfBarcoListBarco);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSocio(socio.getIdSocio()) != null) {
                throw new PreexistingEntityException("Socio " + socio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Socio socio) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Socio persistentSocio = em.find(Socio.class, socio.getIdSocio());
            List<Barco> barcoListOld = persistentSocio.getBarcoList();
            List<Barco> barcoListNew = socio.getBarcoList();
            List<Barco> attachedBarcoListNew = new ArrayList<Barco>();
            for (Barco barcoListNewBarcoToAttach : barcoListNew) {
                barcoListNewBarcoToAttach = em.getReference(barcoListNewBarcoToAttach.getClass(), barcoListNewBarcoToAttach.getIdBarco());
                attachedBarcoListNew.add(barcoListNewBarcoToAttach);
            }
            barcoListNew = attachedBarcoListNew;
            socio.setBarcoList(barcoListNew);
            socio = em.merge(socio);
            for (Barco barcoListOldBarco : barcoListOld) {
                if (!barcoListNew.contains(barcoListOldBarco)) {
                    barcoListOldBarco.setIdSocio(null);
                    barcoListOldBarco = em.merge(barcoListOldBarco);
                }
            }
            for (Barco barcoListNewBarco : barcoListNew) {
                if (!barcoListOld.contains(barcoListNewBarco)) {
                    Socio oldIdSocioOfBarcoListNewBarco = barcoListNewBarco.getIdSocio();
                    barcoListNewBarco.setIdSocio(socio);
                    barcoListNewBarco = em.merge(barcoListNewBarco);
                    if (oldIdSocioOfBarcoListNewBarco != null && !oldIdSocioOfBarcoListNewBarco.equals(socio)) {
                        oldIdSocioOfBarcoListNewBarco.getBarcoList().remove(barcoListNewBarco);
                        oldIdSocioOfBarcoListNewBarco = em.merge(oldIdSocioOfBarcoListNewBarco);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = socio.getIdSocio();
                if (findSocio(id) == null) {
                    throw new NonexistentEntityException("The socio with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Socio socio;
            try {
                socio = em.getReference(Socio.class, id);
                socio.getIdSocio();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The socio with id " + id + " no longer exists.", enfe);
            }
            List<Barco> barcoList = socio.getBarcoList();
            for (Barco barcoListBarco : barcoList) {
                barcoListBarco.setIdSocio(null);
                barcoListBarco = em.merge(barcoListBarco);
            }
            em.remove(socio);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Socio> findSocioEntities() {
        return findSocioEntities(true, -1, -1);
    }

    public List<Socio> findSocioEntities(int maxResults, int firstResult) {
        return findSocioEntities(false, maxResults, firstResult);
    }

    private List<Socio> findSocioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Socio.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Socio findSocio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Socio.class, id);
        } finally {
            em.close();
        }
    }

    public int getSocioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Socio> rt = cq.from(Socio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
