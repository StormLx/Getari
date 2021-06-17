# Fonctions Annuler / Récupérer
![forthebadge](https://forthebadge.com/images/badges/powered-by-coffee.svg)![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)

## Le fonctionnement théorique

Un point de sauvegarde se crée et vous pouvez ainsi annuler ou récupérer les événements passés lorsque vous effectuez une action:
- Modifier le nom de l'évaluation.
- Ajouter/supprimer/modifier un indicateur.
- Ajouter/supprimer/modifier un processus.
- Modifier le paramètrage d'une agrégation.



Bouton annuler (Undo):
- Annule l'état actuel et recharge l'état précédent (CTRL + Z ou en cliquant sur la flèche retour).
- Annule l'état sélectionné dans le menu déroulant et recharge l'état précédent.
Tous les éléments situés entre l'état actuel et l'état sélectionné dans le menu déroulant seront récupérable par ordre d’exécution dans la liste **Récupérer**.

Bouton récupérer (Redo):
- Récupère l'état qui vient d'être annulé et le recharge (CTRL + Y ou en cliquant sur la flèche avant).
- Récupère l'état sélectionné dans le menu déroulant et le recharge.
Tous les éléments situés entre l'état actuel et l'état sélectionné dans le menu déroulant seront de nouveau annulable par ordre d'exécution dans la liste **Annuler**.

## Le fonctionnement du code

 La sauvegarde de l'état actuel se fait grâce à la classe Memento.
 On crée un objet Memento qui prend l'évaluation actuelle en paramètre, ainsi que son état et un ID.
 Chaque action crée un objet Memento et le stock en première position dans une liste observable de la classe History: la liste undo. Chaque ajout vide la deuxième liste (redo) .
 Cette classe History possède deux liste observable qui géreront les éléments à annuler, et ceux à récupérer.
 
 Lors du clique sur le bouton **Annuler**, la méthode undo() de la classe History est appelée.
 Cette méthode transfert le premier élément de la liste undo vers la liste redo et recharge le nouveau premier élément de la liste undo (qui est en fait l'état précédent).

Lors du clique sur le bouton **Récupérer**, la méthode redo() de la classe History est appelée.
Cette méthode transfert le premier élément de la liste redo vers la liste undo en première position et recharge le premier élément de la liste undo.

Lors du clique sur un des éléments du menu déroulant du bouton **Annuler**, la méthode undoByID(int id) de la classe History est appelée.
Cette méthode prend un argument un Integer et cherche dans la liste undo le mémento qui possède cet ID.
Puis, à partir du premier élément jusqu’à celui possédant l'ID, transfert tous les éléments de la liste undo vers la liste redo par ordre d'exécution.

Lors du clique sur un des éléments du menu déroulant du bouton **Récupérer**, la méthode redoByID(int id) de la classe History est appelée.
Cette méthode prend un argument un Integer et cherche dans la liste redo le mémento qui possède cet ID.
Puis, à partir du premier élément jusqu’à celui possédant l'ID, transfert tous les éléments de la liste redo vers la liste undo par ordre d'exécution.
