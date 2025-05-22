@Entity
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResena;

    @ManyToOne
    @JoinColumn(name = "idPatron")
    private Patron patron;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    private String imagen;
    private int puntuacion;
    private String mensaje;

    // getters y setters
}
