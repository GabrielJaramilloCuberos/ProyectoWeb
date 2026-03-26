import { Rol } from "../rol";

export type UsuarioLogin = {
  id_usuario: number;
  nombre: string;
  email: string;
  estado: boolean;
  rol: Rol;
};