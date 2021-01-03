DATA SEGMENT
	start DD
	stop DD
	incr DD
DATA ENDS
CODE SEGMENT
	in eax
	mov start, eax
	push eax
	in eax
	mov stop, eax
	push eax
	in eax
	mov incr, eax
	push eax
debut_while_1:
	mov ebx, stop
	mov eax, start
	sub eax, ebx
	jle vrai_lte_1
	mov eax, 0
	jmp sortie_lte_1
vrai_lte_1:
	mov eax, 1
sortie_lte_1:
	jz sortie_while_1
	mov eax, start
	out eax
	mov ebx, incr
	mov eax, start
	add eax, ebx
	push eax
	pop eax
	mov start, eax
	push eax
	jmp debut_while_1
sortie_while_1:
CODE ENDS
