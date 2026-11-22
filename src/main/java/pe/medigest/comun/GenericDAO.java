package pe.medigest.comun;

import java.util.List;

/** CRUD básico que comparten los DAO (patrón DAO). */
public interface GenericDAO<T> {
    void insertar(T objeto);

    void actualizar(T objeto);

    void eliminar(int id);

    T buscarPorId(int id);

    List<T> listar();
}
